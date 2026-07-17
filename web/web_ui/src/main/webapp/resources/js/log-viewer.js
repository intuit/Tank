(function(window, $) {
    "use strict";

    var P = window.TankLogProcessing;
    var MAX_ENTRIES = 2000;
    var MAX_RAW_CHARACTERS = 5000000;
    var CONTEXT_WINDOW = 5;
    var instances = Object.create(null);

    var MAX_POLL_SECONDS = 300;

    function numberFrom(value, fallback) {
        if (value === null || value === undefined || value === "") {
            return fallback;
        }
        if (typeof value === "string") {
            value = value.replace(/,/g, "").trim();
        }
        var parsed = Number(value);
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    function clampPollSeconds(seconds) {
        var value = numberFrom(seconds, 5);
        if (value < 0) {
            return 0;
        }
        return Math.min(value, MAX_POLL_SECONDS);
    }

    function textSpan(className, text) {
        var span = document.createElement("span");
        span.className = className;
        span.textContent = text;
        return span;
    }

    function highlightText(text, query) {
        var container = document.createDocumentFragment();
        var value = String(text || "");
        var needle = String(query || "").trim();
        if (!needle || needle.indexOf(":") >= 0 || needle.charAt(0) === "-") {
            container.appendChild(document.createTextNode(value));
            return container;
        }
        var lower = value.toLowerCase();
        var lowerNeedle = needle.toLowerCase();
        var start = 0;
        var index = lower.indexOf(lowerNeedle);
        if (index < 0) {
            container.appendChild(document.createTextNode(value));
            return container;
        }
        while (index >= 0) {
            if (index > start) {
                container.appendChild(document.createTextNode(value.slice(start, index)));
            }
            var mark = document.createElement("mark");
            mark.textContent = value.slice(index, index + needle.length);
            container.appendChild(mark);
            start = index + needle.length;
            index = lower.indexOf(lowerNeedle, start);
        }
        if (start < value.length) {
            container.appendChild(document.createTextNode(value.slice(start)));
        }
        return container;
    }

    function LogViewer(root) {
        if (!P) {
            throw new Error("TankLogProcessing failed to load");
        }
        if (!$ || typeof $.ajax !== "function") {
            throw new Error("jQuery is required for the log viewer");
        }

        this.root = root;
        this.widgetVar = root.getAttribute("data-widget") || root.id;
        this.url = root.getAttribute("data-url");
        this.fileName = root.getAttribute("data-file") || "log";
        this.initialLines = Math.max(0, numberFrom(root.getAttribute("data-lines"), 20));
        this.pollMilliseconds = clampPollSeconds(root.getAttribute("data-poll")) * 1000;
        this.offset = null;
        this.timer = null;
        this.request = null;
        this.destroyed = false;
        this.stopped = false;
        this.paused = false;
        this.follow = true;
        this.wrap = false;
        this.pendingLine = "";
        this.openEvent = null;
        this.events = [];
        this.visibleIndexes = [];
        this.selected = new Set();
        this.lastSelectedIndex = null;
        this.focusIndex = -1;
        this.totalReceivedLines = 0;
        this.omittedEvents = 0;
        this.newEntries = 0;
        this.query = "";
        this.parsedQuery = [];
        this.facets = {};
        this.patternFilter = null;
        this.activeLevels = new Set(P.LEVELS.slice());
        this.view = "timeline";
        this.retryDelay = this.pollMilliseconds || 5000;
        this.lastRefreshAt = null;
        this.filterTimer = null;
        this.redactExports = true;

        this.cacheDom();
        this.rawEditor = window.CodeMirror.fromTextArea(this.rawTextArea, {
            mode: "text/plain",
            lineNumbers: true,
            lineWrapping: false,
            readOnly: true,
            theme: "eclipse"
        });
        this.rawLength = 0;

        if (instances[this.widgetVar]) {
            instances[this.widgetVar].destroy();
        }
        instances[this.widgetVar] = this;
        window[this.widgetVar] = this;

        this.bindEvents();
        this.setView("timeline");
        this.applyPreset("all");
        this.setStatus("loading", "Loading");
        this.showNotice("Fetching " + this.fileName + " …");
        this.updateEmptyState();
        this.updateSummary();
        if (!this.url) {
            this.stopped = true;
            this.setStatus("error", "No URL");
            this.showNotice("Log file URL is missing. Reload the page and try Load log again.");
            return;
        }
        this.fetchLog();
    }

    LogViewer.prototype.cacheDom = function() {
        var root = this.root;
        this.timelinePanel = root.querySelector('[data-role="timeline"]');
        this.patternsPanel = root.querySelector('[data-role="patterns"]');
        this.rawPanel = root.querySelector('[data-role="raw"]');
        this.entriesElement = root.querySelector('[data-role="entries"]');
        this.patternList = root.querySelector('[data-role="pattern-list"]');
        this.emptyElement = root.querySelector('[data-role="empty"]');
        this.patternEmpty = root.querySelector('[data-role="pattern-empty"]');
        this.statusElement = root.querySelector('[data-role="status"]');
        this.summaryElement = root.querySelector('[data-role="summary"]');
        this.noticeElement = root.querySelector('[data-role="notice"]');
        this.metaElement = root.querySelector('[data-role="meta"]');
        this.volumeElement = root.querySelector('[data-role="volume"]');
        this.searchElement = root.querySelector('[data-role="search"]');
        this.detailPanel = root.querySelector('[data-role="detail"]');
        this.detailFields = root.querySelector('[data-role="detail-fields"]');
        this.detailRaw = root.querySelector('[data-role="detail-raw"]');
        this.detailContext = root.querySelector('[data-role="detail-context"]');
        this.detailTitle = root.querySelector('[data-role="detail-title"]');
        this.exportPanel = root.querySelector('[data-role="export-panel"]');
        this.exportStats = root.querySelector('[data-role="export-stats"]');
        this.toastElement = root.querySelector('[data-role="toast"]');
        this.rawTextArea = root.querySelector('[data-role="raw-text"]');
        this.redactToggle = root.querySelector('[data-role="redact-toggle"]');
        this.exportScope = root.querySelector('[data-role="export-scope"]');
        this.facetHosts = {
            job: root.querySelector('[data-role="facet-job"]'),
            instance: root.querySelector('[data-role="facet-instance"]'),
            transaction: root.querySelector('[data-role="facet-transaction"]'),
            eventType: root.querySelector('[data-role="facet-eventType"]'),
            loggerShort: root.querySelector('[data-role="facet-loggerShort"]')
        };
    };

    LogViewer.prototype.bindEvents = function() {
        var viewer = this;

        this.root.querySelector('[data-action="pause"]').addEventListener("click", function() {
            viewer.paused = !viewer.paused;
            this.textContent = viewer.paused ? "Resume" : "Pause";
            this.title = viewer.paused
                    ? "Resume live polling for new log bytes"
                    : "Pause live polling. Click again to resume.";
            this.setAttribute("aria-pressed", String(viewer.paused));
            if (viewer.paused) {
                viewer.setStatus("paused", "Paused");
            } else {
                window.clearTimeout(viewer.timer);
                if (!viewer.request) {
                    viewer.fetchLog();
                }
            }
        });

        this.root.querySelector('[data-action="follow"]').addEventListener("click", function() {
            viewer.follow = true;
            viewer.newEntries = 0;
            viewer.scrollToEnd();
            viewer.updateButtons();
        });

        this.root.querySelector('[data-action="wrap"]').addEventListener("click", function() {
            viewer.wrap = !viewer.wrap;
            viewer.root.classList.toggle("log-viewer-wrap", viewer.wrap);
            viewer.rawEditor.setOption("lineWrapping", viewer.wrap);
            this.textContent = viewer.wrap ? "No wrap" : "Wrap";
            this.title = viewer.wrap
                    ? "Turn off wrapping so each log line stays on one row"
                    : "Wrap long lines to the panel width";
            this.setAttribute("aria-pressed", String(viewer.wrap));
        });

        this.root.querySelector('[data-action="refresh"]').addEventListener("click", function() {
            window.clearTimeout(viewer.timer);
            if (!viewer.request) {
                viewer.fetchLog();
            }
        });

        this.root.querySelector('[data-action="clear-search"]').addEventListener("click", function() {
            viewer.searchElement.value = "";
            viewer.query = "";
            viewer.parsedQuery = [];
            viewer.applyFilters();
            viewer.searchElement.focus();
        });

        this.searchElement.addEventListener("input", function() {
            window.clearTimeout(viewer.filterTimer);
            viewer.filterTimer = window.setTimeout(function() {
                viewer.query = viewer.searchElement.value.trim();
                viewer.parsedQuery = P.parseQuery(viewer.query);
                viewer.applyFilters();
            }, 150);
        });

        this.root.querySelectorAll("[data-view]").forEach(function(button) {
            button.addEventListener("click", function() {
                viewer.setView(button.getAttribute("data-view"));
            });
        });

        this.root.querySelectorAll("[data-preset]").forEach(function(button) {
            button.addEventListener("click", function() {
                viewer.applyPreset(button.getAttribute("data-preset"));
            });
        });

        this.root.querySelector('[data-action="copy-selected"]').addEventListener("click", function() {
            viewer.copyBundle("selected", "compact");
        });
        this.root.querySelector('[data-action="copy-visible"]').addEventListener("click", function() {
            viewer.copyBundle("visible", "compact");
        });
        this.root.querySelector('[data-action="copy-context"]').addEventListener("click", function() {
            viewer.copyBundle("context", "compact");
        });
        this.root.querySelector('[data-action="copy-menu"]').addEventListener("click", function() {
            viewer.exportPanel.hidden = !viewer.exportPanel.hidden;
            if (!viewer.exportPanel.hidden) {
                viewer.updateExportStats();
            }
        });
        this.root.querySelector('[data-action="copy-export"]').addEventListener("click", function() {
            var format = viewer.root.querySelector('input[data-export-format]:checked').getAttribute("data-export-format");
            viewer.copyBundle(viewer.exportScope.value, format);
        });
        this.redactToggle.addEventListener("change", function() {
            if (!this.checked) {
                var ok = window.confirm("Disable redaction for copied exports? Secrets may be included.");
                if (!ok) {
                    this.checked = true;
                    return;
                }
            }
            viewer.redactExports = this.checked;
            viewer.updateExportStats();
        });
        this.exportScope.addEventListener("change", function() {
            viewer.updateExportStats();
        });
        this.root.querySelectorAll('input[data-export-format]').forEach(function(input) {
            input.addEventListener("change", function() {
                viewer.updateExportStats();
            });
        });

        this.root.querySelector('[data-action="close-detail"]').addEventListener("click", function() {
            viewer.detailPanel.hidden = true;
        });
        this.root.querySelector('[data-action="copy-event"]').addEventListener("click", function() {
            viewer.copyBundle("focused", "compact");
        });
        this.root.querySelector('[data-action="copy-event-json"]').addEventListener("click", function() {
            viewer.copyBundle("focused", "jsonl");
        });
        this.root.querySelector('[data-action="show-context"]').addEventListener("click", function() {
            viewer.showDetail(viewer.focusIndex, true);
        });

        this.entriesElement.addEventListener("scroll", function() {
            var distanceFromBottom = viewer.entriesElement.scrollHeight
                    - viewer.entriesElement.scrollTop
                    - viewer.entriesElement.clientHeight;
            viewer.follow = distanceFromBottom < 40;
            if (viewer.follow) {
                viewer.newEntries = 0;
            }
            viewer.updateButtons();
        });

        this.root.addEventListener("keydown", function(event) {
            if (event.target && (event.target.tagName === "INPUT" || event.target.tagName === "TEXTAREA" || event.target.tagName === "SELECT")) {
                if (event.key === "Escape") {
                    event.target.blur();
                }
                return;
            }
            if (event.key === "/") {
                event.preventDefault();
                viewer.searchElement.focus();
                return;
            }
            if (event.key === "Escape") {
                viewer.selected.clear();
                viewer.detailPanel.hidden = true;
                viewer.renderVisibleRows();
                return;
            }
            if (event.key === "j" || event.key === "ArrowDown") {
                event.preventDefault();
                viewer.moveFocus(1);
            } else if (event.key === "k" || event.key === "ArrowUp") {
                event.preventDefault();
                viewer.moveFocus(-1);
            } else if (event.key === "Enter" && viewer.focusIndex >= 0) {
                event.preventDefault();
                viewer.showDetail(viewer.focusIndex, true);
            } else if (event.key === "c" && (event.metaKey || event.ctrlKey) && viewer.selected.size) {
                // allow native copy; also support explicit shortcut without modifier below
            } else if (event.key === "c" && !event.metaKey && !event.ctrlKey) {
                event.preventDefault();
                viewer.copyBundle(viewer.selected.size ? "selected" : "focused", "compact");
            }
        });
    };

    LogViewer.prototype.applyPreset = function(preset) {
        var viewer = this;
        if (preset === "errors") {
            this.activeLevels = new Set(["ERROR", "FATAL"]);
        } else if (preset === "warnplus") {
            this.activeLevels = new Set(["WARN", "ERROR", "FATAL"]);
        } else {
            this.activeLevels = new Set(P.LEVELS.slice());
        }
        this.root.querySelectorAll("[data-preset]").forEach(function(button) {
            button.setAttribute("aria-pressed", String(button.getAttribute("data-preset") === preset));
        });
        this.applyFilters();
    };

    LogViewer.prototype.fetchLog = function() {
        var viewer = this;
        if (this.destroyed || this.stopped || this.request) {
            return;
        }
        if (!document.documentElement.contains(this.root)) {
            this.destroy();
            return;
        }
        if (this.paused) {
            this.scheduleNextFetch(this.pollMilliseconds);
            return;
        }

        var requestedOffset = this.offset === null ? -this.initialLines : this.offset;
        this.setStatus("loading", this.offset === null ? "Loading" : "Refreshing");

        var restartAfterRotation = false;
        this.request = $.ajax({
            url: this.url,
            data: { from: requestedOffset },
            dataType: "text",
            cache: false,
            global: false,
            timeout: 60000
        }).done(function(data, ignoredStatus, xhr) {
            try {
                var totalLength = numberFrom(xhr.getResponseHeader("X-Total-Content-Length"), null);
                var contentStart = numberFrom(xhr.getResponseHeader("X-Content-Start"), requestedOffset);
                var rotated = viewer.offset !== null
                        && requestedOffset >= 0
                        && contentStart < requestedOffset;

                if (rotated) {
                    viewer.clearContent();
                    viewer.showNotice("The log was truncated or rotated. Display restarted from the new file.");
                    viewer.offset = null;
                    restartAfterRotation = true;
                    return;
                }
                if (viewer.stopped === false) {
                    viewer.hideNotice();
                }

                viewer.appendRaw(data);
                viewer.appendStructured(data, viewer.pollMilliseconds === 0);
                viewer.offset = totalLength !== null
                        ? totalLength
                        : Math.max(0, contentStart) + new TextEncoder().encode(data || "").length;
                viewer.lastRefreshAt = new Date();
                viewer.retryDelay = viewer.pollMilliseconds || 5000;
                viewer.updateMeta(totalLength);
                if (viewer.paused) {
                    viewer.setStatus("paused", "Paused");
                } else {
                    viewer.setStatus(viewer.pollMilliseconds > 0 ? "live" : "loaded",
                            viewer.pollMilliseconds > 0 ? "Live" : "Loaded");
                }
                if (!data && viewer.events.length === 0) {
                    viewer.showNotice("The log response was empty. The file may be empty or the server returned no bytes.");
                }
            } catch (error) {
                viewer.setStatus("error", "Parse error");
                viewer.showNotice("Loaded the log, but failed to render it: " + (error && error.message ? error.message : error));
            }
        }).fail(function(xhr, status) {
            if (status === "abort" || viewer.destroyed) {
                return;
            }
            if (status === "timeout") {
                viewer.setStatus("error", "Timed out");
                viewer.showNotice("Log request timed out after 60s for " + viewer.url + ". Retrying…");
                viewer.retryDelay = Math.min((viewer.retryDelay || 5000) * 2, 60000);
                return;
            }
            if (xhr.status === 401 || xhr.status === 403) {
                viewer.stopped = true;
                viewer.setStatus("error", "Access denied");
                viewer.showNotice("Your session cannot access this log. Reload after signing in again.");
                return;
            }
            if (xhr.status === 404) {
                viewer.stopped = true;
                viewer.setStatus("error", "Missing");
                viewer.showNotice("HTTP 404 for " + viewer.url
                        + ". The UI lists Tomcat logs, but the API could not find that file. Redeploy the latest WAR so both use ${catalina.base}/logs.");
                return;
            }
            viewer.setStatus("error", "Disconnected");
            viewer.showNotice("Log request failed ("
                    + (xhr.status || status || "error")
                    + "). Retrying with backoff. URL: " + viewer.url);
            viewer.retryDelay = Math.min((viewer.retryDelay || 5000) * 2, 60000);
        }).always(function() {
            viewer.request = null;
            if (viewer.destroyed) {
                return;
            }
            if (restartAfterRotation) {
                viewer.fetchLog();
                return;
            }
            viewer.scheduleNextFetch(viewer.retryDelay);
            viewer.updateEmptyState();
        });
    };

    LogViewer.prototype.scheduleNextFetch = function(delay) {
        if (this.destroyed || this.stopped || this.pollMilliseconds <= 0) {
            return;
        }
        window.clearTimeout(this.timer);
        this.timer = window.setTimeout(this.fetchLog.bind(this), delay || this.pollMilliseconds);
    };

    LogViewer.prototype.appendStructured = function(data, flushPartialLine) {
        var result = P.normalizeChunk(data || "", {
            pendingLine: this.pendingLine,
            openEvent: this.openEvent,
            flush: !!flushPartialLine
        });
        this.pendingLine = result.pendingLine;
        this.openEvent = result.openEvent;

        var incoming = result.events;
        if (!incoming.length) {
            this.applyFilters();
            return;
        }

        this.totalReceivedLines += incoming.reduce(function(sum, event) {
            return sum + (event.rawLines ? event.rawLines.length : 1);
        }, 0);

        if (incoming.length > MAX_ENTRIES) {
            this.omittedEvents += this.events.length + incoming.length - MAX_ENTRIES;
            incoming = incoming.slice(incoming.length - MAX_ENTRIES);
            this.events = [];
            this.selected.clear();
        } else if (this.events.length + incoming.length > MAX_ENTRIES) {
            var removeCount = this.events.length + incoming.length - MAX_ENTRIES;
            this.omittedEvents += removeCount;
            this.events.splice(0, removeCount);
            this.reindexSelection(-removeCount);
        }

        for (var i = 0; i < incoming.length; i++) {
            this.events.push(incoming[i]);
        }

        this.applyFilters();
        if (this.follow) {
            this.scrollToEnd();
        } else {
            this.newEntries += incoming.length;
            this.updateButtons();
        }
    };

    LogViewer.prototype.reindexSelection = function(delta) {
        var next = new Set();
        this.selected.forEach(function(index) {
            var shifted = index + delta;
            if (shifted >= 0) {
                next.add(shifted);
            }
        });
        this.selected = next;
        if (this.focusIndex >= 0) {
            this.focusIndex = Math.max(-1, this.focusIndex + delta);
        }
        if (this.lastSelectedIndex !== null) {
            this.lastSelectedIndex = Math.max(0, this.lastSelectedIndex + delta);
        }
    };

    LogViewer.prototype.applyFilters = function() {
        var viewer = this;
        this.visibleIndexes = [];
        for (var i = 0; i < this.events.length; i++) {
            var event = this.events[i];
            if (!this.activeLevels.has(event.level)) {
                continue;
            }
            if (!P.matchesQuery(event, this.parsedQuery)) {
                continue;
            }
            if (!P.matchesFacets(event, this.facets)) {
                continue;
            }
            if (this.patternFilter && event.fingerprint !== this.patternFilter) {
                continue;
            }
            this.visibleIndexes.push(i);
        }
        this.renderVolume();
        this.renderFacets();
        this.renderPatterns();
        this.renderVisibleRows();
        this.updateSummary();
        this.updateEmptyState();
        this.updateExportStats();
    };

    LogViewer.prototype.updateEmptyState = function() {
        if (!this.emptyElement) {
            return;
        }
        var loading = this.statusElement
                && this.statusElement.className.indexOf("status-loading") >= 0;
        if (this.view !== "timeline") {
            this.emptyElement.hidden = true;
            return;
        }
        if (this.visibleIndexes.length > 0) {
            this.emptyElement.hidden = true;
            return;
        }
        this.emptyElement.hidden = false;
        if (loading && this.events.length === 0) {
            this.emptyElement.textContent = "Loading log data…";
            return;
        }
        if (this.events.length > 0) {
            var levels = Array.from(this.activeLevels).join(", ");
            this.emptyElement.textContent = "No events match the current filters ("
                    + this.events.length + " retained, levels: " + levels
                    + "). Click All or clear search/facets.";
            return;
        }
        this.emptyElement.textContent = "Waiting for log data. If this stays empty, check the yellow notice above for HTTP errors or path mismatches.";
    };

    LogViewer.prototype.renderVisibleRows = function() {
        var viewer = this;
        var fragment = document.createDocumentFragment();
        this.visibleIndexes.forEach(function(eventIndex, visiblePos) {
            fragment.appendChild(viewer.createRow(viewer.events[eventIndex], eventIndex, visiblePos));
        });
        this.entriesElement.replaceChildren(fragment);
        this.updateEmptyState();
    };

    LogViewer.prototype.createRow = function(event, eventIndex, visiblePos) {
        var viewer = this;
        var row = document.createElement("div");
        row.className = "log-entry log-level-" + event.level.toLowerCase();
        row.setAttribute("role", "option");
        row.setAttribute("data-index", String(eventIndex));
        row.setAttribute("aria-selected", String(this.selected.has(eventIndex)));
        if (this.selected.has(eventIndex)) {
            row.classList.add("is-selected");
        }
        if (this.focusIndex === eventIndex) {
            row.classList.add("is-focused");
        }

        var check = document.createElement("input");
        check.type = "checkbox";
        check.className = "log-entry-check";
        check.checked = this.selected.has(eventIndex);
        check.addEventListener("click", function(domEvent) {
            domEvent.stopPropagation();
            viewer.toggleSelect(eventIndex, domEvent.shiftKey, true);
        });

        var body = document.createElement("button");
        body.type = "button";
        body.className = "log-entry-summary";
        body.appendChild(textSpan("log-entry-time", event.timestampDisplay || "—"));
        body.appendChild(textSpan("log-entry-level", event.level));
        var source = [event.job && ("job=" + event.job), event.transaction && ("txn=" + event.transaction.slice(0, 8)),
            event.loggerShort || event.logger || "plain"].filter(Boolean).join(" · ");
        body.appendChild(textSpan("log-entry-source", source));
        var message = document.createElement("span");
        message.className = "log-entry-message";
        message.appendChild(highlightText(event.message || "—", this.plainSearchTerm()));
        body.appendChild(message);

        body.addEventListener("click", function(domEvent) {
            viewer.focusIndex = eventIndex;
            if (domEvent.shiftKey) {
                viewer.toggleSelect(eventIndex, true, true);
            } else if (domEvent.metaKey || domEvent.ctrlKey) {
                viewer.toggleSelect(eventIndex, false, true);
            } else {
                viewer.selected.clear();
                viewer.selected.add(eventIndex);
                viewer.lastSelectedIndex = eventIndex;
            }
            viewer.showDetail(eventIndex, false);
            viewer.renderVisibleRows();
        });

        row.appendChild(check);
        row.appendChild(body);
        row.dataset.visiblePos = String(visiblePos);
        return row;
    };

    LogViewer.prototype.plainSearchTerm = function() {
        for (var i = 0; i < this.parsedQuery.length; i++) {
            if (!this.parsedQuery[i].field && !this.parsedQuery[i].negated) {
                return this.parsedQuery[i].value;
            }
        }
        return "";
    };

    LogViewer.prototype.toggleSelect = function(eventIndex, shiftKey, additive) {
        if (shiftKey && this.lastSelectedIndex !== null) {
            var start = Math.min(this.lastSelectedIndex, eventIndex);
            var end = Math.max(this.lastSelectedIndex, eventIndex);
            for (var i = start; i <= end; i++) {
                this.selected.add(i);
            }
        } else if (additive) {
            if (this.selected.has(eventIndex)) {
                this.selected.delete(eventIndex);
            } else {
                this.selected.add(eventIndex);
            }
        } else {
            this.selected.clear();
            this.selected.add(eventIndex);
        }
        this.lastSelectedIndex = eventIndex;
        this.focusIndex = eventIndex;
    };

    LogViewer.prototype.moveFocus = function(delta) {
        if (!this.visibleIndexes.length) {
            return;
        }
        var currentPos = this.visibleIndexes.indexOf(this.focusIndex);
        if (currentPos < 0) {
            currentPos = delta > 0 ? -1 : 0;
        }
        var nextPos = Math.max(0, Math.min(this.visibleIndexes.length - 1, currentPos + delta));
        this.focusIndex = this.visibleIndexes[nextPos];
        this.renderVisibleRows();
        var focused = this.entriesElement.querySelector('[data-index="' + this.focusIndex + '"]');
        if (focused) {
            focused.scrollIntoView({ block: "nearest" });
        }
    };

    LogViewer.prototype.showDetail = function(eventIndex, withContext) {
        if (eventIndex < 0 || eventIndex >= this.events.length) {
            this.detailPanel.hidden = true;
            return;
        }
        var event = this.events[eventIndex];
        this.focusIndex = eventIndex;
        this.detailPanel.hidden = false;
        this.detailTitle.textContent = event.level + " · " + (event.loggerShort || event.logger || "event");
        this.detailFields.replaceChildren();
        var fields = [
            ["Timestamp", event.timestampDisplay || event.timestamp],
            ["Level", event.level],
            ["Logger", event.logger],
            ["Thread", event.thread],
            ["Job", event.job],
            ["Instance", event.instance],
            ["Transaction", event.transaction],
            ["Event type", event.eventType],
            ["Project", event.project],
            ["Plan", event.plan],
            ["Script", event.script],
            ["Step", event.step],
            ["HTTP URL", event.httpUrl],
            ["HTTP RT ms", event.httpRtMs],
            ["Validation", event.validationStatus],
            ["Exception", event.exceptionType],
            ["Fingerprint", event.fingerprint]
        ];
        fields.forEach(function(pair) {
            if (!pair[1]) {
                return;
            }
            var dt = document.createElement("dt");
            dt.textContent = pair[0];
            var dd = document.createElement("dd");
            dd.textContent = pair[1];
            this.detailFields.appendChild(dt);
            this.detailFields.appendChild(dd);
        }, this);
        this.detailRaw.textContent = (event.rawLines || [event.raw]).join("\n");
        if (withContext || true) {
            var start = Math.max(0, eventIndex - CONTEXT_WINDOW);
            var end = Math.min(this.events.length - 1, eventIndex + CONTEXT_WINDOW);
            var lines = [];
            for (var i = start; i <= end; i++) {
                var prefix = i === eventIndex ? ">" : " ";
                lines.push(prefix + " " + (this.events[i].timestampDisplay || "")
                        + " " + this.events[i].level
                        + " " + (this.events[i].message || "").slice(0, 200));
            }
            this.detailContext.textContent = lines.join("\n");
        }
    };

    LogViewer.prototype.renderFacets = function() {
        var viewer = this;
        Object.keys(this.facetHosts).forEach(function(field) {
            var host = viewer.facetHosts[field];
            if (!host) {
                return;
            }
            host.replaceChildren();
            var counts = P.facetCounts(viewer.events.filter(function(event) {
                if (!viewer.activeLevels.has(event.level) || !P.matchesQuery(event, viewer.parsedQuery)) {
                    return false;
                }
                var facetCopy = Object.assign({}, viewer.facets);
                delete facetCopy[field];
                return P.matchesFacets(event, facetCopy)
                        && (!viewer.patternFilter || event.fingerprint === viewer.patternFilter);
            }), field, 8);
            if (!counts.length) {
                host.textContent = "—";
                return;
            }
            counts.forEach(function(item) {
                var button = document.createElement("button");
                button.type = "button";
                button.className = "log-facet-chip";
                var active = viewer.facets[field] === item.value;
                button.setAttribute("aria-pressed", String(active));
                button.textContent = item.value + " (" + item.count + ")";
                button.title = (active ? "Clear filter " : "Filter to ") + field + "=" + item.value;
                button.addEventListener("click", function() {
                    if (viewer.facets[field] === item.value) {
                        delete viewer.facets[field];
                    } else {
                        viewer.facets[field] = item.value;
                    }
                    viewer.applyFilters();
                });
                host.appendChild(button);
            });
        });
    };

    LogViewer.prototype.renderPatterns = function() {
        var viewer = this;
        var visibleEvents = this.visibleIndexes.map(function(index) {
            return viewer.events[index];
        });
        var patterns = P.buildPatterns(visibleEvents, { max: 40 });
        this.patternList.replaceChildren();
        this.patternEmpty.hidden = patterns.length !== 0 || this.view !== "patterns";
        patterns.forEach(function(pattern) {
            var row = document.createElement("article");
            row.className = "log-pattern log-level-" + pattern.level.toLowerCase();
            if (viewer.patternFilter === pattern.fingerprint) {
                row.classList.add("is-active");
            }
            var title = document.createElement("button");
            title.type = "button";
            title.className = "log-pattern-summary";
            title.innerHTML = "";
            title.appendChild(textSpan("log-pattern-count", "x" + pattern.count));
            title.appendChild(textSpan("log-entry-level", pattern.level));
            title.appendChild(textSpan("log-entry-source", pattern.logger || "unknown"));
            title.appendChild(textSpan("log-entry-message", pattern.message || ""));
            title.title = (viewer.patternFilter === pattern.fingerprint
                    ? "Clear this pattern filter"
                    : "Show only this pattern") + " (x" + pattern.count + ")";
            title.addEventListener("click", function() {
                viewer.patternFilter = viewer.patternFilter === pattern.fingerprint ? null : pattern.fingerprint;
                viewer.setView("timeline");
                viewer.applyFilters();
            });
            var actions = document.createElement("div");
            actions.className = "log-pattern-actions";
            var only = document.createElement("button");
            only.type = "button";
            only.textContent = "Only";
            only.title = "Keep only this pattern in the timeline";
            only.addEventListener("click", function() {
                viewer.patternFilter = pattern.fingerprint;
                viewer.setView("timeline");
                viewer.applyFilters();
            });
            var exclude = document.createElement("button");
            exclude.type = "button";
            exclude.textContent = "Exclude";
            exclude.title = "Hide messages like this from the current filter set";
            exclude.addEventListener("click", function() {
                viewer.searchElement.value = (viewer.searchElement.value + ' -"' + String(pattern.message || "").slice(0, 80) + '"').trim();
                viewer.query = viewer.searchElement.value;
                viewer.parsedQuery = P.parseQuery(viewer.query);
                viewer.patternFilter = null;
                viewer.applyFilters();
            });
            actions.appendChild(only);
            actions.appendChild(exclude);
            row.appendChild(title);
            row.appendChild(textSpan("log-pattern-meta",
                    (pattern.firstTs || "") + " → " + (pattern.lastTs || "")
                    + (pattern.eventType ? " · " + pattern.eventType : "")));
            row.appendChild(actions);
            viewer.patternList.appendChild(row);
        });
    };

    LogViewer.prototype.renderVolume = function() {
        var buckets = P.volumeBuckets(this.visibleIndexes.map(function(index) {
            return this.events[index];
        }, this), 32);
        this.volumeElement.replaceChildren();
        var max = buckets.reduce(function(current, bucket) {
            return Math.max(current, bucket.total);
        }, 1);
        buckets.forEach(function(bucket) {
            var bar = document.createElement("span");
            bar.className = "log-volume-bar";
            var height = Math.max(2, Math.round((bucket.total / max) * 100));
            bar.style.height = height + "%";
            if (bucket.error) {
                bar.classList.add("has-error");
            } else if (bucket.warn) {
                bar.classList.add("has-warn");
            }
            bar.title = bucket.total + " events";
            this.volumeElement.appendChild(bar);
        }, this);
    };

    LogViewer.prototype.eventsForScope = function(scope) {
        var viewer = this;
        if (scope === "selected") {
            return Array.from(this.selected).sort(function(a, b) {
                return a - b;
            }).map(function(index) {
                return viewer.events[index];
            }).filter(Boolean);
        }
        if (scope === "focused") {
            return this.focusIndex >= 0 ? [this.events[this.focusIndex]] : [];
        }
        if (scope === "context") {
            if (this.focusIndex < 0 && !this.selected.size) {
                return [];
            }
            var center = this.focusIndex >= 0 ? this.focusIndex : Array.from(this.selected)[0];
            var start = Math.max(0, center - CONTEXT_WINDOW);
            var end = Math.min(this.events.length - 1, center + CONTEXT_WINDOW);
            return this.events.slice(start, end + 1);
        }
        if (scope === "retained") {
            return this.events.slice();
        }
        return this.visibleIndexes.map(function(index) {
            return viewer.events[index];
        });
    };

    LogViewer.prototype.buildExport = function(scope, format) {
        var events = this.eventsForScope(scope);
        if (!events.length) {
            return null;
        }
        var filters = {
            query: this.query || "",
            levels: Array.from(this.activeLevels),
            facets: this.facets,
            pattern: this.patternFilter || ""
        };
        if (format === "jsonl") {
            return P.formatJsonl(events, { redact: this.redactExports });
        }
        return P.formatCompactBundle({
            fileName: this.fileName,
            events: events,
            rawLines: this.totalReceivedLines,
            retained: this.events.length,
            filters: filters,
            redact: this.redactExports,
            includeEvidence: scope === "selected" || scope === "focused" || scope === "context",
            timelineLimit: scope === "retained" ? 120 : 80
        });
    };

    LogViewer.prototype.updateExportStats = function() {
        if (!this.exportPanel || this.exportPanel.hidden) {
            return;
        }
        var format = this.root.querySelector('input[data-export-format]:checked').getAttribute("data-export-format");
        var bundle = this.buildExport(this.exportScope.value, format);
        if (!bundle) {
            this.exportStats.textContent = "Select or filter events to export.";
            return;
        }
        var redaction = Object.keys(bundle.redactionCounts || {}).map(function(type) {
            return type + "=" + bundle.redactionCounts[type];
        }).join(" ") || "none";
        this.exportStats.textContent = bundle.eventCount + " events · "
                + bundle.characters + " chars · ~" + bundle.tokens + " tokens · redaction " + redaction;
    };

    LogViewer.prototype.copyBundle = function(scope, format) {
        var viewer = this;
        var bundle = this.buildExport(scope, format);
        if (!bundle) {
            this.showToast("Nothing to copy for the current selection.");
            return;
        }
        P.copyText(bundle.text).then(function() {
            viewer.showToast("Copied " + bundle.eventCount + " events (~" + bundle.tokens + " tokens)");
        }).catch(function() {
            viewer.showToast("Copy failed. Select the export text manually if needed.");
        });
    };

    LogViewer.prototype.showToast = function(message) {
        var viewer = this;
        this.toastElement.hidden = false;
        this.toastElement.textContent = message;
        window.clearTimeout(this.toastTimer);
        this.toastTimer = window.setTimeout(function() {
            viewer.toastElement.hidden = true;
        }, 2500);
    };

    LogViewer.prototype.appendRaw = function(data) {
        if (!data) {
            return;
        }
        var documentModel = this.rawEditor.getDoc();
        if (data.length >= MAX_RAW_CHARACTERS) {
            data = data.slice(data.length - MAX_RAW_CHARACTERS);
            var firstNewline = data.indexOf("\n");
            data = firstNewline >= 0 ? data.slice(firstNewline + 1) : data;
            documentModel.setValue(data);
            this.rawLength = data.length;
            return;
        }
        if (this.rawLength + data.length > MAX_RAW_CHARACTERS) {
            var retained = documentModel.getValue() + data;
            retained = retained.slice(retained.length - MAX_RAW_CHARACTERS);
            var newline = retained.indexOf("\n");
            retained = newline >= 0 ? retained.slice(newline + 1) : retained;
            documentModel.setValue(retained);
            this.rawLength = retained.length;
            return;
        }
        var lastLine = documentModel.lastLine();
        documentModel.replaceRange(data, {
            line: lastLine,
            ch: documentModel.getLine(lastLine).length
        });
        this.rawLength += data.length;
        if (this.follow && this.view === "raw") {
            this.rawEditor.scrollIntoView({ line: documentModel.lastLine(), ch: 0 });
        }
    };

    LogViewer.prototype.setView = function(view) {
        this.view = view;
        this.timelinePanel.hidden = view !== "timeline";
        this.patternsPanel.hidden = view !== "patterns";
        this.rawPanel.hidden = view !== "raw";
        this.root.querySelectorAll("[data-view]").forEach(function(button) {
            button.setAttribute("aria-pressed", String(button.getAttribute("data-view") === view));
        });
        if (view === "raw") {
            this.rawEditor.refresh();
            if (this.follow) {
                var documentModel = this.rawEditor.getDoc();
                this.rawEditor.scrollIntoView({ line: documentModel.lastLine(), ch: 0 });
            }
        } else if (view === "patterns") {
            this.renderPatterns();
        } else if (this.follow) {
            this.scrollToEnd();
        }
        this.updateEmptyState();
    };

    LogViewer.prototype.setStatus = function(state, label) {
        this.statusElement.className = "log-viewer-status status-" + state;
        this.statusElement.textContent = label;
    };

    LogViewer.prototype.showNotice = function(message) {
        this.noticeElement.textContent = message;
        this.noticeElement.hidden = false;
    };

    LogViewer.prototype.hideNotice = function() {
        this.noticeElement.hidden = true;
        this.noticeElement.textContent = "";
    };

    LogViewer.prototype.updateMeta = function(totalLength) {
        var parts = [];
        if (this.lastRefreshAt) {
            parts.push("refreshed " + this.lastRefreshAt.toLocaleTimeString());
        }
        if (totalLength !== null && totalLength !== undefined) {
            parts.push(totalLength + " bytes");
        }
        parts.push("poll " + (this.pollMilliseconds / 1000) + "s");
        this.metaElement.textContent = parts.join(" · ");
    };

    LogViewer.prototype.updateSummary = function() {
        var message = "Showing " + this.visibleIndexes.length
                + " of " + this.events.length + " retained events";
        if (this.totalReceivedLines > 0) {
            message += " · " + this.totalReceivedLines + " raw lines received";
        }
        if (this.omittedEvents > 0) {
            message += " · " + this.omittedEvents + " older events omitted";
        }
        if (this.selected.size) {
            message += " · " + this.selected.size + " selected";
        }
        if (this.patternFilter) {
            message += " · pattern filter on";
        }
        this.summaryElement.textContent = message;
    };

    LogViewer.prototype.updateButtons = function() {
        var followButton = this.root.querySelector('[data-action="follow"]');
        followButton.textContent = this.follow
                ? "Following"
                : "Follow" + (this.newEntries > 0 ? " (" + this.newEntries + " new)" : "");
        followButton.title = this.follow
                ? "Keep the timeline scrolled to the newest events. Turns off when you scroll up."
                : "Jump to the newest events and resume auto-follow"
                        + (this.newEntries > 0 ? " (" + this.newEntries + " arrived while scrolled up)" : "");
        followButton.setAttribute("aria-pressed", String(this.follow));
    };

    LogViewer.prototype.scrollToEnd = function() {
        this.entriesElement.scrollTop = this.entriesElement.scrollHeight;
        this.newEntries = 0;
        this.updateButtons();
    };

    LogViewer.prototype.clearContent = function() {
        this.events = [];
        this.visibleIndexes = [];
        this.selected.clear();
        this.focusIndex = -1;
        this.lastSelectedIndex = null;
        this.entriesElement.replaceChildren();
        this.patternList.replaceChildren();
        this.rawEditor.setValue("");
        this.rawLength = 0;
        this.pendingLine = "";
        this.openEvent = null;
        this.totalReceivedLines = 0;
        this.omittedEvents = 0;
        this.newEntries = 0;
        this.updateSummary();
    };

    LogViewer.prototype.destroy = function() {
        this.destroyed = true;
        this.stopped = true;
        window.clearTimeout(this.timer);
        window.clearTimeout(this.filterTimer);
        window.clearTimeout(this.toastTimer);
        if (this.request) {
            this.request.abort();
            this.request = null;
        }
        if (this.rawEditor) {
            this.rawEditor.toTextArea();
            this.rawEditor = null;
        }
        if (instances[this.widgetVar] === this) {
            delete instances[this.widgetVar];
        }
        if (window[this.widgetVar] === this) {
            try {
                delete window[this.widgetVar];
            } catch (ignored) {
                window[this.widgetVar] = undefined;
            }
        }
    };

    window.TankLogViewer = {
        instances: instances,
        destroyAll: function() {
            Object.keys(instances).forEach(function(key) {
                instances[key].destroy();
            });
        },
        mount: function(root) {
            if (!root) {
                return null;
            }
            try {
                if (!window.TankLogProcessing) {
                    throw new Error("TankLogProcessing is required");
                }
                var widget = root.getAttribute("data-widget") || root.id;
                if (instances[widget]) {
                    instances[widget].destroy();
                }
                return new LogViewer(root);
            } catch (error) {
                var status = root.querySelector('[data-role="status"]');
                var notice = root.querySelector('[data-role="notice"]');
                if (status) {
                    status.className = "log-viewer-status status-error";
                    status.textContent = "Failed";
                }
                if (notice) {
                    notice.hidden = false;
                    notice.textContent = "Log viewer failed to start: "
                            + (error && error.message ? error.message : error);
                }
                return null;
            }
        }
    };
})(window, window.jQuery);
