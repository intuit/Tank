/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.owasp.proxy.daemon.LoopAvoidingTargetedConnectionHandler;
import org.owasp.proxy.daemon.Proxy;
import org.owasp.proxy.daemon.ServerGroup;
import org.owasp.proxy.daemon.TargetedConnectionHandler;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.client.HttpClient;
import org.owasp.proxy.http.server.BufferedMessageInterceptor;
import org.owasp.proxy.http.server.DefaultHttpRequestHandler;
import org.owasp.proxy.http.server.HttpProxyConnectionHandler;
import org.owasp.proxy.http.server.HttpRequestHandler;
import org.owasp.proxy.socks.SocksConnectionHandler;
import org.owasp.proxy.ssl.SSLConnectionHandler;
import org.owasp.proxy.ssl.SSLContextSelector;

import com.intuit.tank.conversation.Session;
import com.intuit.tank.conversation.Transaction;
import com.intuit.tank.entity.Application;
import com.intuit.tank.handler.BufferingHttpRequestHandler;
import com.intuit.tank.proxy.config.ProxyConfiguration;
import com.intuit.tank.proxy.settings.ui.ProxyConfigDialog;
import com.intuit.tank.proxy.settings.ui.XmlFileFilter;
import com.intuit.tank.proxy.table.ShowHostsDialog;
import com.intuit.tank.proxy.table.TransactionRecordedListener;
import com.intuit.tank.proxy.table.TransactionTable;
import com.intuit.tank.proxy.table.TransactionTableModel;
import com.intuit.tank.util.WebConversationJaxbParseXML;

/**
 * ProxyApp
 * 
 * @author dangleton
 * 
 */
public class ProxyApp extends JFrame implements TransactionRecordedListener {

    private static final long serialVersionUID = 1L;
    private Application application;
    private Proxy p;
    private TransactionTableModel model;
    private JDialog detailsDialog;
    private JFileChooser fileChooser;
    private JTextArea detailsTF;

    private AbstractAction startAction;

    private AbstractAction stopAction;
    private AbstractAction pauseAction;
    private AbstractAction settingsAction;
    private AbstractAction saveAction;
    private AbstractAction openAction;
    private AbstractAction filterAction;

    private ProxyConfigDialog configDialog;

    private int keyMask;
    private File currentFile;
    private boolean isDirty;
    private ShowHostsDialog showHostsDialog;
    private AbstractAction showHostsAction;

    public ProxyApp() {
        super("Intuit Tank Recording Proxy");
        setSize(new Dimension(800, 600));
        setBounds(new Rectangle(getSize()));
        setPreferredSize(getSize());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                try {
                    stop();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
            }
        });
        keyMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        configDialog = new ProxyConfigDialog(this);
        createActions();
        this.setJMenuBar(createMenu());
        createDetailsDialog();

        JPanel tablePanel = getTransactionTable();
        this.add(createToolBar(), BorderLayout.NORTH);
        this.add(tablePanel, BorderLayout.CENTER);

        WindowUtil.centerOnScreen(this);
        pack();
        setVisible(true);
        requestFocus();
    }

    /**
     * @inheritDoc
     */
    public void transactionProcessed(Transaction t, boolean filtered) {
        model.addTransaction(t, filtered);
    }

    /**
     * @throws IOException
     * @throws GeneralSecurityException
     * 
     */
    private void start() throws GeneralSecurityException, IOException {
        if (application != null && application.isPaused()) {
            application.resumeSession();
        } else {
            ProxyConfiguration config = configDialog.getConfiguration();
            model.clear();
            saveAction.setEnabled(false);
            filterAction.setEnabled(false);
            application = new Application(config);
            currentFile = new File(config.getOutputFile());
            isDirty = false;
            InetSocketAddress listen;
            String proxy = "DIRECT";
            listen = new InetSocketAddress("localhost", config.getPort());

            final ProxySelector ps = Main.getProxySelector(proxy);

            DefaultHttpRequestHandler drh = new DefaultHttpRequestHandler() {
                @Override
                protected HttpClient createClient() {
                    HttpClient client = super.createClient();
                    client.setProxySelector(ps);
                    client.setSoTimeout(0);
                    return client;
                }
            };
            ServerGroup sg = new ServerGroup();
            sg.addServer(listen);
            drh.setServerGroup(sg);
            HttpRequestHandler rh = drh;

            BufferedMessageInterceptor bmi = new BufferedMessageInterceptor() {
                @Override
                public Action directResponse(RequestHeader request,
                        MutableResponseHeader response) {
                    return Action.BUFFER;
                }

                @Override
                public Action directRequest(MutableRequestHeader request) {
                    return Action.BUFFER;
                }
            };
            rh = new BufferingHttpRequestHandler(rh, bmi, 4 * 1024 * 1024, application);

            HttpProxyConnectionHandler hpch = new HttpProxyConnectionHandler(rh);
            SSLContextSelector cp = Main.getSSLContextSelector();
            TargetedConnectionHandler tch = new SSLConnectionHandler(cp, true, hpch);
            tch = new LoopAvoidingTargetedConnectionHandler(sg, tch);
            hpch.setConnectHandler(tch);
            TargetedConnectionHandler socks = new SocksConnectionHandler(tch, true);
            application.startSession(this);
            p = new Proxy(listen, socks, null);
            p.setSocketTimeout(0);
            p.start();
            System.out.println("Listener started on " + listen);
        }
        startAction.setEnabled(false);
        stopAction.setEnabled(true);
        pauseAction.setEnabled(true);
        saveAction.setEnabled(false);
        filterAction.setEnabled(false);
        openAction.setEnabled(false);
    }

    private void stop() throws JAXBException, IOException {
        if (p != null) {
            p.stop();
            application.endSession();
            p = null;
        }
        startAction.setEnabled(true);
        stopAction.setEnabled(false);
        pauseAction.setEnabled(false);
        saveAction.setEnabled(true);
        filterAction.setEnabled(true);
        openAction.setEnabled(true);
        application = null;
        saveAction.setEnabled(isDirty);
    }

    /**
     * 
     */
    private void pause() {
        application.pauseSession();
        pauseAction.setEnabled(false);
        startAction.setEnabled(true);
    }

    /**
     * 
     */
    private void showHosts() {
        if (showHostsDialog == null) {
            showHostsDialog = new ShowHostsDialog(this, configDialog);
        }
        showHostsDialog.setHosts(model.getHostSet());
        showHostsDialog.setVisible(true);
    }

    /**
     * 
     */
    private void openRecording() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileChooser.getSelectedFile();
            try {
                List<Transaction> transactions = new WebConversationJaxbParseXML().parse(new FileReader(fileChooser
                        .getSelectedFile()));
                model.setTransactions(transactions);
                saveAction.setEnabled(false);
                filterAction.setEnabled(true);
                currentFile = fileChooser.getSelectedFile();
            } catch (Exception e) {
                JOptionPane
                        .showMessageDialog(this, "Error opening recording: " + e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    /**
     * 
     */
    private void save() {
        if (currentFile != null) {
            saveAction.setEnabled(false);
            JAXBContext ctx;
            try {
                ctx = JAXBContext.newInstance(Session.class.getPackage().getName());
                Marshaller m = ctx.createMarshaller();
                m.setProperty("jaxb.formatted.output", Boolean.TRUE);
                Session session = new Session(model.getTransactions(), configDialog.getConfiguration()
                        .isFollowRedirects());
                System.out.println("outputting to : " + currentFile.getCanonicalPath());
                m.marshal(session, currentFile);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error saving recording: " + e, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 
     */
    private void showSettings() {
        configDialog.showDialog();
    }

    private void createDetailsDialog() {
        detailsDialog = new JDialog(this);
        detailsDialog.setModal(true);
        detailsDialog.setLayout(new BorderLayout());

        detailsTF = new JTextArea();
        detailsTF.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(detailsTF);
        detailsDialog.add(scrollPane, BorderLayout.CENTER);
        detailsDialog.setModal(true);
        detailsDialog.setSize(new Dimension(500, 300));
        detailsDialog.setBounds(new Rectangle(getSize()));
        detailsDialog.setPreferredSize(getSize());
        WindowUtil.centerOnScreen(detailsDialog);
    }

    private JPanel getTransactionTable() {
        JPanel frame = new JPanel(new BorderLayout());
        model = new TransactionTableModel();
        final JTable table = new TransactionTable(model);
        final TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(model);
        table.setRowSorter(sorter);
        final JPopupMenu pm = new JPopupMenu();
        JMenuItem item = new JMenuItem("Delete Selected");
        item.addActionListener((ActionEvent arg0) -> {
            int[] selectedRows = table.getSelectedRows();
            if (selectedRows.length != 0) {
                int response = JOptionPane.showConfirmDialog(ProxyApp.this, "Are you sure you want to delete "
                        + selectedRows.length + " Transactions?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (response == JOptionPane.YES_OPTION) {
                    int[] correctedRows = new int[selectedRows.length];
                    for (int i = selectedRows.length; --i >= 0;) {
                        int row = selectedRows[i];
                        int index = (Integer) table.getValueAt(row, 0) - 1;
                        correctedRows[i] = index;
                    }
                    Arrays.sort(correctedRows);
                    for (int i = correctedRows.length; --i >= 0;) {
                        int row = correctedRows[i];
                        Transaction transaction = model.getTransactionForIndex(row);
                        if (transaction != null) {
                            model.removeTransaction(transaction, row);
                            isDirty = true;
                            saveAction.setEnabled(isDirty && !stopAction.isEnabled());
                        }
                    }
                }
            }
        });
        pm.add(item);
        table.add(pm);

        table.addMouseListener(new MouseAdapter() {
            boolean pressed = false;

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Point p = e.getPoint();
                    int row = table.rowAtPoint(p);
                    int index = (Integer) table.getValueAt(row, 0) - 1;
                    Transaction transaction = model.getTransactionForIndex(index);
                    if (transaction != null) {
                        detailsTF.setText(transaction.toString());
                        detailsTF.setCaretPosition(0);
                        detailsDialog.setVisible(true);
                    }
                }
            }

            /**
             * @inheritDoc
             */
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    pressed = true;
                    int[] selectedRows = table.getSelectedRows();
                    if (selectedRows.length != 0) {
                        pm.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

            /**
             * @inheritDoc
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (!pressed && e.isPopupTrigger()) {
                    int[] selectedRows = table.getSelectedRows();
                    if (selectedRows.length != 0) {
                        pm.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }

        });

        JScrollPane pane = new JScrollPane(table);
        frame.add(pane, BorderLayout.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Filter: ");
        panel.add(label, BorderLayout.WEST);
        final JLabel countLabel = new JLabel(" Count: 0 ");
        panel.add(countLabel, BorderLayout.EAST);
        final JTextField filterText = new JTextField("");
        filterText.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                String text = filterText.getText();
                if (text.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        sorter.setRowFilter(RowFilter.regexFilter(text));
                        countLabel.setText(" Count: " + sorter.getViewRowCount() + " ");
                    } catch (PatternSyntaxException pse) {
                        System.err.println("Bad regex pattern");
                    }
                }
            }
        });
        panel.add(filterText, BorderLayout.CENTER);

        frame.add(panel, BorderLayout.NORTH);
        return frame;
    }

    private JToolBar createToolBar() {
        JToolBar ret = new JToolBar();
        // ret.setBackground(new Color(111,167,209));

        ret.add(Box.createHorizontalStrut(5));
        ret.add(createButton(openAction));
        ret.add(Box.createHorizontalStrut(5));
        ret.add(createButton(saveAction));

        ret.addSeparator();

        ret.add(Box.createHorizontalStrut(5));
        ret.add(createButton(startAction));
        ret.add(Box.createHorizontalStrut(5));
        ret.add(createButton(stopAction));
        ret.add(Box.createHorizontalStrut(5));
        ret.add(createButton(pauseAction));
        ret.add(Box.createHorizontalStrut(5));

        ret.addSeparator();

        ret.add(Box.createHorizontalStrut(5));
        ret.add(createButton(filterAction));
        ret.add(createButton(settingsAction));
        ret.addSeparator();

        ret.add(Box.createHorizontalStrut(5));
        ret.add(createButton(showHostsAction));

        ret.add(Box.createHorizontalGlue());

        return ret;
    }

    private JButton createButton(Action a) {
        JButton ret = new JButton(a);
        ret.setText("");
        ret.setMargin(new Insets(3, 3, 3, 3));
        // ret.setIcon((Icon) a.getValue(Action.SMALL_ICON_KEY));
        return ret;

    }

    @SuppressWarnings("serial")
    public JMenuBar createMenu() {
        JMenuBar ret = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu sessionMenu = new JMenu("Session");
        ret.add(fileMenu);
        ret.add(sessionMenu);

        fileMenu.add(getMenuItem(openAction));
        fileMenu.add(getMenuItem(saveAction));
        fileMenu.addSeparator();
        fileMenu.add(getMenuItem(filterAction));
        fileMenu.add(getMenuItem(settingsAction));
        fileMenu.addSeparator();
        AbstractAction quitAction = new AbstractAction("Quit") {
            public void actionPerformed(ActionEvent arg0) {
                System.exit(0);
            }
        };
        quitAction.putValue(javax.swing.Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Q, keyMask));
        quitAction.putValue(javax.swing.Action.MNEMONIC_KEY, KeyEvent.VK_Q);

        fileMenu.add(new JMenuItem(quitAction));
        sessionMenu.add(getMenuItem(startAction));
        sessionMenu.add(getMenuItem(stopAction));
        sessionMenu.add(getMenuItem(pauseAction));
        sessionMenu.addSeparator();
        sessionMenu.add(getMenuItem(showHostsAction));

        return ret;
    }

    private JMenuItem getMenuItem(Action a) {
        JMenuItem item = new JMenuItem(a);
        return item;
    }

    /**
     * 
     */
    @SuppressWarnings("serial")
    private void createActions() {

        startAction = new AbstractAction("Start Recording", loadImage("icons/16/control_play_blue.png")) {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    start();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ProxyApp.this, "Error statrting proxy: " + e.toString(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        };
        startAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Start Recording");

        stopAction = new AbstractAction("Stop Recording", loadImage("icons/16/control_stop_blue.png")) {
            public void actionPerformed(ActionEvent arg0) {
                try {
                    stop();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ProxyApp.this, "Error stopping proxy: " + e.toString(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

        };
        stopAction.setEnabled(false);
        stopAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Stop Recording");

        pauseAction = new AbstractAction("Pause Recording", loadImage("icons/16/control_pause_blue.png")) {
            public void actionPerformed(ActionEvent arg0) {
                pause();
            }
        };
        pauseAction.setEnabled(false);
        pauseAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Pause Recording");

        settingsAction = new AbstractAction("Settings", loadImage("icons/16/cog.png")) {
            public void actionPerformed(ActionEvent arg0) {
                showSettings();
            }

        };
        settingsAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Settings");

        filterAction = new AbstractAction("Run Filters", loadImage("icons/16/filter.png")) {
            public void actionPerformed(ActionEvent arg0) {
                filter();
            }

        };
        filterAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Run Filters");
        filterAction.setEnabled(false);

        saveAction = new AbstractAction("Save", loadImage("icons/16/save_as.png")) {
            public void actionPerformed(ActionEvent arg0) {
                save();
            }

        };
        saveAction.setEnabled(false);
        saveAction.putValue(javax.swing.Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, keyMask));
        saveAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Save");

        openAction = new AbstractAction("Open Recording...", loadImage("icons/16/folder_go.png")) {
            public void actionPerformed(ActionEvent arg0) {
                openRecording();
            }

        };
        openAction.putValue(javax.swing.Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, keyMask));
        openAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Open Recording...");

        showHostsAction = new AbstractAction("Hosts...", loadImage("icons/16/page_add.png")) {
            public void actionPerformed(ActionEvent arg0) {
                showHosts();
            }

        };
        showHostsAction.putValue(javax.swing.Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_H, keyMask));
        showHostsAction.putValue(javax.swing.Action.SHORT_DESCRIPTION, "Show Hosts...");

        fileChooser = new JFileChooser(new File("."));
        fileChooser.setDialogTitle("Open Recording...");
        fileChooser.setFileFilter(new XmlFileFilter());
    }

    /**
     * 
     */
    protected void filter() {
        List<Transaction> transactions = model.getAllTransactions();
        model.clear();
        for (Transaction t : transactions) {
            boolean filtered = true;
            if (Application.shouldInclude(t, configDialog.getInclusions(),
                    configDialog.getExclusions(), false)) {
                filtered = false;
            }
            transactionProcessed(t, filtered);
        }
    }

    private ImageIcon loadImage(String path) {
        URL url = ClassLoader.getSystemClassLoader().getResource(path);
        return new ImageIcon(url);
    }

    public static void main(String[] args) {
        if (StringUtils.isBlank(System.getProperty("https.protocols"))) {
            System.setProperty("https.protocols", "TLSv1.2,SSLv3");
        }
        Security.addProvider(new BouncyCastleProvider()); // add it
        new ProxyApp();
    }

}
