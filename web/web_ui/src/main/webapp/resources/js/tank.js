//$(document).ready(function() {
//    $("table").addClass("stripeMe");
//    $("#header>table").removeClass("stripeMe");
//    $("table>tbody>tr:even").addClass("alt");
//    applyTableRowHovering();
//    $("table").ajaxEnd(function() {
//        applyTableRowHovering();
//    });
//});
//
//function applyTableRowHovering() {
//    $(".stripeMe tbody>tr").mouseover(function() {
//        $(this).addClass("over");
//    }).mouseout(function() {
//        $(this).removeClass("over");
//    });
//}
//
//function onFilter() {
//    applyTableRowHovering();
//}

var scrollingDiv;
var scrollDivOffset;

var performResizeSwitch;
$(document).ready(function() {
	$(window).resize(function(){
	  clearTimeout(performResizeSwitch);
	  performResizeSwitch = setTimeout(globalSetScreenSizes, 100);
	});
	globalSetScreenSizes();
    scrollingDiv = $(".scrollingDiv");
    if(scrollingDiv.length){
        scrollDivOffset = scrollingDiv.offset().top;
    }

    selectAndFocusField(".formInput:visible:enabled:first");
    applyTableRowHovering();
    scrollRegions();


    $(this).ajaxComplete(function() {
        applyTableRowHovering();
        scrollRegions();
    });
});

function scrollRegions() {
    $(window).scroll(function() {
        if(scrollingDiv == null || scrollDivOffset == null){
            return;
        }
        try {
            var x = $(window).scrollTop();
            if (x > scrollDivOffset) {
                x = x - scrollDivOffset;
                scrollingDiv.stop().animate({
                    "marginTop" : x + "px"
                }, "slow");
            } else {
                scrollingDiv.stop().animate({
                    "marginTop" : 0 + "px"
                }, "slow");
            }
        } catch (exception) {
        }
    });
}

function selectAndFocusField(selector) {
    $(selector).focus();
    $(selector).select();
}

function applyTableRowHovering() {
    $(".striped > tbody>tr:even").addClass("alt");
    $(".highlight > tbody>tr").mouseover(function() {
        if ($(this).hasClass("alt")) {
            $(this).toggleClass("altPresent");
            $(this).toggleClass("alt");
        }
        $(this).addClass("over");
    }).mouseout(function() {
        if ($(this).hasClass("altPresent")) {
            $(this).toggleClass("altPresent");
            $(this).toggleClass("alt");
        }
        $(this).removeClass("over");
    });

}

function showStatus() {
    $("#ajaxStatus").show();
}

function hideStatus() {
    $("#ajaxStatus").hide();
}

jQuery.fn.center = function() {
    this.css("position", "absolute");
    this.css("top", (($(window).height() - this.outerHeight()) / 2)
            + $(window).scrollTop() + "px");
    this.css("left", (($(window).width() - this.outerWidth()) / 2)
            + $(window).scrollLeft() + "px");
    return this;
};


var scrollPos = 0;
var scrollLeft = 0;
function saveScrollPos() {
    scrollPos = $(".ui-datatable-scrollable-body").scrollTop();
    scrollLeft = $(".ui-datatable-scrollable-body").scrollLeft();
}

function getScrollPos() {
$(".ui-datatable-scrollable-body").scrollTop(scrollPos);
$(".ui-datatable-scrollable-body").scrollLeft(scrollLeft);
}

function globalSetScreenSizes() {
	var sHeight = $(window).height(); // New height
	var sWidth = $(window).width(); // New width
	try {
	setScreenSizes(sWidth, sHeight);
		if (typeof resizeFinished === "function") {
				resizeFinished();
		}
	} catch(err) {}
}

/*
 * Date Format 1.2.3
 * (c) 2007-2009 Steven Levithan <stevenlevithan.com>
 * MIT license
 *
 * Includes enhancements by Scott Trenda <scott.trenda.net>
 * and Kris Kowal <cixar.com/~kris.kowal/>
 *
 * Accepts a date, a mask, or a date and a mask.
 * Returns a formatted version of the given date.
 * The date defaults to the current date/time.
 * The mask defaults to dateFormat.masks.default.
 */

var dateFormat = function () {
	var	token = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloSZ]|"[^"]*"|'[^']*'/g,
		timezone = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
		timezoneClip = /[^-+\dA-Z]/g,
		pad = function (val, len) {
			val = String(val);
			len = len || 2;
			while (val.length < len) val = "0" + val;
			return val;
		};

	// Regexes and supporting functions are cached through closure
	return function (date, mask, utc) {
		var dF = dateFormat;

		// You can't provide utc if you skip other args (use the "UTC:" mask prefix)
		if (arguments.length == 1 && Object.prototype.toString.call(date) == "[object String]" && !/\d/.test(date)) {
			mask = date;
			date = undefined;
		}

		// Passing date through Date applies Date.parse, if necessary
		date = date ? new Date(date) : new Date;
		if (isNaN(date)) throw SyntaxError("invalid date");

		mask = String(dF.masks[mask] || mask || dF.masks["default"]);

		// Allow setting the utc argument via the mask
		if (mask.slice(0, 4) == "UTC:") {
			mask = mask.slice(4);
			utc = true;
		}

		var	_ = utc ? "getUTC" : "get",
			d = date[_ + "Date"](),
			D = date[_ + "Day"](),
			m = date[_ + "Month"](),
			y = date[_ + "FullYear"](),
			H = date[_ + "Hours"](),
			M = date[_ + "Minutes"](),
			s = date[_ + "Seconds"](),
			L = date[_ + "Milliseconds"](),
			o = utc ? 0 : date.getTimezoneOffset(),
			flags = {
				d:    d,
				dd:   pad(d),
				ddd:  dF.i18n.dayNames[D],
				dddd: dF.i18n.dayNames[D + 7],
				m:    m + 1,
				mm:   pad(m + 1),
				mmm:  dF.i18n.monthNames[m],
				mmmm: dF.i18n.monthNames[m + 12],
				yy:   String(y).slice(2),
				yyyy: y,
				h:    H % 12 || 12,
				hh:   pad(H % 12 || 12),
				H:    H,
				HH:   pad(H),
				M:    M,
				MM:   pad(M),
				s:    s,
				ss:   pad(s),
				l:    pad(L, 3),
				L:    pad(L > 99 ? Math.round(L / 10) : L),
				t:    H < 12 ? "a"  : "p",
				tt:   H < 12 ? "am" : "pm",
				T:    H < 12 ? "A"  : "P",
				TT:   H < 12 ? "AM" : "PM",
				Z:    utc ? "UTC" : (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
				o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4),
				S:    ["th", "st", "nd", "rd"][d % 10 > 3 ? 0 : (d % 100 - d % 10 != 10) * d % 10]
			};

		return mask.replace(token, function ($0) {
			return $0 in flags ? flags[$0] : $0.slice(1, $0.length - 1);
		});
	};
}();

// Some common format strings
dateFormat.masks = {
	"default":      "ddd mmm dd yyyy HH:MM:ss",
	shortDate:      "m/d/yy",
	mediumDate:     "mmm d, yyyy",
	longDate:       "mmmm d, yyyy",
	fullDate:       "dddd, mmmm d, yyyy",
	shortTime:      "h:MM TT",
	mediumTime:     "h:MM:ss TT",
	longTime:       "h:MM:ss TT Z",
	isoDate:        "yyyy-mm-dd",
	isoTime:        "HH:MM:ss",
	isoDateTime:    "yyyy-mm-dd'T'HH:MM:ss",
	isoUtcDateTime: "UTC:yyyy-mm-dd'T'HH:MM:ss'Z'"
};

// Internationalization strings
dateFormat.i18n = {
	dayNames: [
		"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat",
		"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
	],
	monthNames: [
		"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
		"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
	]
};

// For convenience...
Date.prototype.format = function (mask, utc) {
	return dateFormat(this, mask, utc);
};

// V2 API Job/VM Actions
const JOB_ENDPOINT = '/v2/jobs';
const VM_ENDPOINT = '/v2/agent/instance';

function runJobEndpoint(nodeStatus, nodeId, controllerUrl) { //TODO: remove tank_war
	switch(nodeStatus) {
		case 'Created':
			return controllerUrl + JOB_ENDPOINT + '/start/' + nodeId;
		case 'Paused':
		case 'RampPaused':
			return controllerUrl + JOB_ENDPOINT + '/resume/' + nodeId;
		default:
			return controllerUrl + JOB_ENDPOINT + '/start/' + nodeId;
	}
}

function getActionEndpoint(action, nodeId, controllerUrl, endpoint) {
	return `${controllerUrl}${endpoint}/${action}/${nodeId}`;
}

function getJobActionEndpoint(action, nodeStatus, nodeId, controllerUrl) {
	if (action === "run") {
		return runJobEndpoint(nodeStatus, nodeId, controllerUrl);
	}
	return getActionEndpoint(action, nodeId, controllerUrl, JOB_ENDPOINT);
}

function callJobAction(action, nodeId, nodeStatus, nodeType, controllerUrl) {
	let endpoint;
	switch (nodeType) {
		case 'job':
			console.log('Performing ' + action + ' action on job with status: ' + nodeStatus);
			console.log('jobId: ' + nodeId);
			endpoint = getJobActionEndpoint(action, nodeStatus, nodeId, controllerUrl);
			break;
		case 'vm':
			console.log('Performing ' + action + ' action on instance with status: ' + nodeStatus);
			console.log('instanceId: ' + nodeId);
			if (action === "run") {
				endpoint = getActionEndpoint('resume', nodeId, controllerUrl, VM_ENDPOINT);
				break;
			}
			endpoint = getActionEndpoint(action, nodeId, controllerUrl, VM_ENDPOINT);
			break;
		default:
			console.log('Unknown node type: ' + nodeType);
			return;  // don't make the call
	}

	console.log('Node Endpoint: ' + endpoint);

	callEndpoint(action, endpoint)
		.then(() => {
			PF('confirmJobQueueDelete').hide();
			PF('confirmJobQueueKill').hide();
			console.log(`${nodeType} ${nodeId} action call was successful: ${action}`);
		})
		.catch((error) => {
			console.log(`Failed calling ${action} on ${nodeType} ${nodeId}:`, error);
		});
}

async function callEndpoint(action, endpoint) {
	try {
		let response = await fetch(endpoint, {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		});

		if (!response.ok) {
			throw new Error(`Error calling ${action}: ${response.statusText}`);
		}

		let data = await response.text();
		console.log(`Success calling ${action} - Job status: ${data}`);
	} catch (error) {
		console.error(`Error calling ${action}:`, error.message);
		throw error;
	}
}

