package gui;
import console.*;
import models.*;
import exceptions.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;

public class HotelGUI extends JFrame {
    
    //MainPanel
    private JPanel mainPanel;
    private JPanel inputPanel;
    private JPanel listPanel;
    private JLabel statusBar;

    //Input
    private JTextField txtName,txtContact,txtCheckIn,txtNights,txtRate;
    private JComboBox<String> cmbRoomType;
    private JComboBox<String> cmbRate;

    //Standard Room
    private JTextField txtRoomNumber;
    private JComboBox<String> cmbBedType;
    private JCheckBox chkWifi;
    private JLabel lblAvailable, lblOccupied, lblCleaning, lblMaintenance;

    //Suite
    private JTextField txtLevel;
    private JComboBox<String> cmbSuiteType;
    private JCheckBox chkJacuzzi;

    //Conference
    private JTextField txtCapacity;
    private JCheckBox chkProjector;
    private JComboBox<String> cmbHallName;


    //Card
    private CardLayout cardLayout;
    private JPanel cardPanel;

    //List and Display
    private DefaultListModel<String> listModel;
    private JList<String> reservationList;
    private JTextArea txtDetails;

    //Color
    private Color primaryColor = new Color(41, 128, 185);    // Blue
    private Color secondaryColor = new Color(236, 240, 241); // Light gray
    private Color accentColor = new Color(231, 76, 60);      // Red
    private Color successColor = new Color(39, 174, 96);     // Green
    private Color warningColor = new Color(243, 156, 18);    // Orange
    private Color cleaningColor = new Color(155, 89, 182);   // Purple

    int count = 0;


    public HotelGUI() {
        setTitle("Hotel Room System");
        setSize(1150,800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);

        FileHandler.loadFromFile();

        createMenuBar();

        createMainLayout();

        updateStatusBar();

        refreshReservationList();
    }
    
    //Menu Bar
    private void createMenuBar() {
        JMenuBar menuBar =  new JMenuBar();
        menuBar.setBackground(primaryColor);

        //File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setBackground(Color.white);

        JMenuItem newItem = new JMenuItem("New Reservation");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem loadItem = new JMenuItem("Load");
        JMenuItem exitItem = new JMenuItem("Exit");

        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));

        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        newItem.addActionListener(e -> {
            clearInputFields();
        });
        saveItem.addActionListener(e -> {
            FileHandler.saveToFile();
            JOptionPane.showMessageDialog(this, "Reservation saved");
            updateStatusBar();
        });
        loadItem.addActionListener(e -> {
            FileHandler.loadFromFile();
            refreshReservationList();
            updateStatusBar();
            JOptionPane.showMessageDialog(this, "Reservation loaded");
        });
        exitItem.addActionListener(e -> {
            FileHandler.saveToFile();
            System.exit(0);
        });

        
        //View Menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setBackground(Color.white);
        
        JMenuItem listAllItem = new JMenuItem("List All");
        JMenuItem reportItem = new JMenuItem("View Report");
        JMenuItem statusBoardItem = new JMenuItem("Room Status Board");
        //

        listAllItem.addActionListener(e -> refreshReservationList());
        reportItem.addActionListener(e -> showReport());
        statusBoardItem.addActionListener(e -> showStatusBoardDialog());
        
        viewMenu.add(listAllItem);
        viewMenu.add(reportItem);
        viewMenu.add(statusBoardItem);
        

        //guest menu
        JMenu servicesMenu = new JMenu("Guest Services");

        servicesMenu.setBackground(Color.white);

        JMenuItem roomServiceItem = new JMenuItem("Room Service (RM25)");
        JMenuItem laundryItem = new JMenuItem("Laundry (RM15)");
        JMenuItem spaItem = new JMenuItem("Spa (RM100)");
        JMenuItem wakeupItem = new JMenuItem("Wake-up Call (Free)");
        JMenuItem transportItem = new JMenuItem("Transportation (RM50)");

        roomServiceItem.addActionListener(e -> orderGuestService("Room Service"));
        laundryItem.addActionListener(e -> orderGuestService("Laundry"));
        spaItem.addActionListener(e -> orderGuestService("Spa"));
        wakeupItem.addActionListener(e -> orderGuestService("Wake-up Call"));
        transportItem.addActionListener(e -> orderGuestService("Transportation"));
        
        servicesMenu.add(roomServiceItem);
        servicesMenu.add(laundryItem);
        servicesMenu.add(spaItem);
        servicesMenu.add(wakeupItem);
        servicesMenu.add(transportItem);

        //Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setBackground(Color.white);
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAbout());
        
        helpMenu.add(aboutItem);

        //hover menu
        MouseAdapter hover = new MouseAdapter() {
            
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JMenu) e.getSource()).doClick();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                JMenu menu = (JMenu) e.getSource();

                if (menu.getPopupMenu().contains(e.getPoint())) {
                    return;
                }

                MenuSelectionManager.defaultManager().clearSelectedPath();
            }
        };

        //
        fileMenu.addMouseListener(hover);
        viewMenu.addMouseListener(hover);
        helpMenu.addMouseListener(hover);
        servicesMenu.addMouseListener(hover);
        
        //
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);
        menuBar.add(servicesMenu);
        
        setJMenuBar(menuBar);
    }

    private void createMainLayout() {
        mainPanel = new JPanel(new BorderLayout(10,10));
        mainPanel.setBorder(new EmptyBorder(10,10,10,10));
        mainPanel.setBackground(secondaryColor);

        JPanel topPanel = createStatusBoardPanel();
        mainPanel.add(topPanel,BorderLayout.NORTH);
        
        inputPanel = createInputPanel();

        listPanel = createListPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,inputPanel,listPanel);

        splitPane.setDividerLocation(450);

        mainPanel.add(splitPane,BorderLayout.CENTER);

        // JLabel statusBar = new JLabel("Total Reservation: " + ReservationManager.getSize());
        // JLabel statusBar = new JLabel("Total Reservation: " + count++);
        statusBar = new JLabel("Total Reservations: " + ReservationManager.getSize());
        updateTitleStatusBar();
        //updateStatusBar();

        statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        mainPanel.add(statusBar,BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createStatusBoardPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER,20,5));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 2),
            "-Room Status Board",
            TitledBorder.CENTER, TitledBorder. TOP,
            new Font("Arial", Font. BOLD, 14), primaryColor
        ));

        JPanel availPanel = createStatusBox("AVAILABLE", "", successColor);
        lblAvailable = (JLabel) ((JPanel) availPanel.getComponent(0)).getComponent(1);

        JPanel occPanel = createStatusBox("OCCUPIED", "", accentColor);
        lblOccupied = (JLabel) ((JPanel) occPanel.getComponent(0)).getComponent(1);
        
        JPanel cleanPanel = createStatusBox("CLEANING", "", cleaningColor);
        lblCleaning = (JLabel) ((JPanel) cleanPanel.getComponent(0)).getComponent(1);
        
        JPanel maintPanel = createStatusBox("MAINTENANCE", "", warningColor);
        lblMaintenance = (JLabel) ((JPanel) maintPanel.getComponent(0)).getComponent(1);

        panel.add(availPanel);
        panel.add(occPanel);
        panel.add(cleanPanel);
        panel.add(maintPanel);

        return panel;
    }

    private JPanel createStatusBox(String status, String countStatus, Color color) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(color);
        box.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        box.setPreferredSize(new Dimension(120, 60));
        
        JPanel inner = new JPanel(new GridLayout(2, 1));
        inner.setBackground(color);
        
        JLabel lblTitle = new JLabel(status, SwingConstants.CENTER);
        lblTitle.setForeground(Color. WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 10));
        
        JLabel lblCount = new JLabel(countStatus, SwingConstants.CENTER);
        lblCount.setForeground(Color.white);
        lblCount.setFont(new Font("Arial", Font.BOLD, 20));
        
        inner.add(lblTitle);
        inner.add(lblCount);
        box.add(inner);
        
        return box;
    }

    private void updateStatusBar() {
        int[] counts = ReservationManager.getStatusCounts();
        lblAvailable.setText(String.valueOf(counts[0]));
        lblOccupied.setText(String.valueOf(counts[1]));
        lblMaintenance.setText(String.valueOf(counts[2]));
        lblCleaning.setText(String.valueOf(counts[3]));
    }

    private void updateTitleStatusBar() {
        statusBar.setText("Total Reservations: " + ReservationManager.getSize());
    }

    //Input Panel
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(primaryColor,2),"New Reservation",TitledBorder.LEFT,TitledBorder.TOP,new Font("Arial",Font.BOLD,14),primaryColor));
        panel.setBackground(Color.white);

        JPanel commonPanel = new JPanel(new GridLayout(7,2,5,5));
        commonPanel.setBackground(Color.white);
        commonPanel.setBorder(new EmptyBorder(10,10,10,10));

        commonPanel.add(new JLabel("Room Type:"));
        cmbRoomType = new JComboBox<>(new String[]{"Standard Room","Suite Room","Conference Hall"});
        cmbRoomType.addActionListener(e -> switchCardPanel());
        commonPanel.add(cmbRoomType);

        commonPanel.add(new JLabel("Guest Name:"));
        txtName = new JTextField();
        commonPanel.add(txtName);

        commonPanel.add(new JLabel("Contact Info:"));
        txtContact = new JTextField();
        commonPanel.add(txtContact);

        commonPanel.add(new JLabel("Reservation Date:"));
        JPanel datePanel = new JPanel(new BorderLayout());
        txtCheckIn = new JTextField();
        JButton btnPickDate = new JButton("Pick Date");
        btnPickDate.addActionListener(e -> {
            JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(editor);
            int result = JOptionPane.showConfirmDialog(panel, dateSpinner, "Select Date", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
            java.util.Date selected = (java.util.Date) dateSpinner.getValue();
            java.time.LocalDate localDate = selected.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            txtCheckIn.setText(localDate.toString());
            }
        });
        datePanel.add(txtCheckIn, BorderLayout.CENTER);
        datePanel.add(btnPickDate, BorderLayout.EAST);
        commonPanel.add(datePanel);

        commonPanel.add(new JLabel("Number of Nights:"));
        txtNights = new JTextField();
        commonPanel.add(txtNights);

        
        commonPanel.add(new JLabel("Rate per Night:"));
        cmbRate = new JComboBox<>(new String[]{"Normal Day", "Weekend", "Public Holiday"});
        commonPanel.add(cmbRate);
        cmbRate.addActionListener(e -> {
            String selectedRate = (String) cmbRate.getSelectedItem();
            String selectedTypeRoom = (String) cmbRoomType.getSelectedItem();
            if (selectedTypeRoom == "Standard Room") {
                
                switch (selectedRate) {
                    case "Normal Day":
                        txtRate.setText("100.0");
                        break;
                    case "Weekend":
                        txtRate.setText("150.0");
                        break;
                    case "Public Holiday":
                        txtRate.setText("200.0");
                        break;
                    default:
                        txtRate.setText("");
                        break;
                }
            } else if (selectedTypeRoom == "Suite Room") {
                switch (selectedRate) {
                    case "Normal Day":
                        txtRate.setText("200.0");
                        break;
                    case "Weekend":
                        txtRate.setText("300.0");
                        break;
                    case "Public Holiday":
                        txtRate.setText("400.0");
                        break;
                    default:
                        txtRate.setText("");
                        break;
                }
            } else if (selectedTypeRoom == "Conference Hall") {
                
                switch (selectedRate) {
                    case "Normal Day":
                        txtRate.setText("500.0");
                        break;
                    case "Weekend":
                        txtRate.setText("750.0");
                        break;
                    case "Public Holiday":
                        txtRate.setText("1000.0");
                        break;
                    default:
                        txtRate.setText("");
                        break;
                }
            }
        });

        commonPanel.add(new JLabel("Rate per Night:"));
        txtRate = new JTextField();
        commonPanel.add(txtRate);
                        

        //Card Panel
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(Color.white);

        cardPanel.add(createStandardRoomPanel(),"Standard Room");
        cardPanel.add(createSuiteRoomPanel(),"Suite Room");
        cardPanel.add(createConferenceHallPanel(),"Conference Hall");

        //button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,10,10));
        buttonPanel.setBackground(Color.white);

        JButton btnAdd = new JButton("Add Reservation");
        btnAdd.setBackground(successColor);
        btnAdd.setForeground(Color.white);
        btnAdd.setFont(new Font("Arial",Font.BOLD,12));
        btnAdd.addActionListener(e -> addReservation());

        JButton btnClear = new JButton("Clear");
        btnClear.setBackground(accentColor);
        btnClear.setForeground(Color.white);
        btnClear.setFont(new Font("Arial",Font.BOLD,12));
        btnClear.addActionListener(e -> clearInputFields());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.white);
        topPanel.add(commonPanel,BorderLayout.NORTH);
        topPanel.add(cardPanel,BorderLayout.CENTER);

        panel.add(topPanel,BorderLayout.CENTER);
        panel.add(buttonPanel,BorderLayout.SOUTH);

        return panel;
    }

    //JPanel createStandardRoomPanel
    private JPanel createStandardRoomPanel() {
        JPanel panel = new JPanel(new GridLayout(3,2,5,5));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createTitledBorder("Standard Room Details"));

        panel.add(new JLabel("Room Number:"));
        txtRoomNumber = new JTextField();
        panel.add(txtRoomNumber);

        panel.add(new JLabel("Bed Type:"));
        cmbBedType = new JComboBox<>(new String[]{"Single","Double","Queen","King"});
        panel.add(cmbBedType);

        panel.add(new JLabel("Has Wifi:"));
        chkWifi = new JCheckBox();
        panel.add(chkWifi);

        return panel;
    }

    //SuiteRoomPanel
    private JPanel createSuiteRoomPanel() {
        JPanel panel = new JPanel(new GridLayout(3,2,5,5));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createTitledBorder("Suite Room Details"));

        panel.add(new JLabel("Level:"));
        txtLevel = new JTextField();
        panel.add(txtLevel);

        panel.add(new JLabel("Suite Type:"));
        cmbSuiteType = new JComboBox<>(new String[]{"Junior","Executive","Presidential"});
        panel.add(cmbSuiteType);

        panel.add(new JLabel("Has Jacuzzi:"));
        chkJacuzzi = new JCheckBox();
        panel.add(chkJacuzzi);

        return panel;
    };
    //ConferenceHallPanel
    private JPanel createConferenceHallPanel() {
        JPanel panel = new JPanel(new GridLayout(3,2,5,5));
        panel.setBackground(Color.white);
        panel.setBorder(BorderFactory.createTitledBorder("Conference Hall Details"));

        panel.add(new JLabel("Hall Name:"));
        cmbHallName = new JComboBox<>(new String[]{"Grand Ballroom", "Emerald Hall", "Crystal Hall"});
        panel.add(cmbHallName);

        panel.add(new JLabel("Capacity:"));
        txtCapacity = new JTextField();
        panel.add(txtCapacity);

        panel.add(new JLabel("Has Projector:"));
        chkProjector = new JCheckBox();
        panel.add(chkProjector);

        return panel;
    }

    //List Panel
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        panel.setBorder(BorderFactory. createTitledBorder(
            BorderFactory.createLineBorder(primaryColor, 2),
            "Reservations",
            TitledBorder. LEFT, TitledBorder.TOP,
            new Font("Arial", Font. BOLD, 14), primaryColor
        ));
        panel.setBackground(Color. WHITE);

        listModel = new DefaultListModel<>();
        reservationList = new JList<>(listModel);
        reservationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationList.setFont(new Font("Monospaced", Font. PLAIN, 12));
        reservationList.addListSelectionListener(e -> showSelectedDetails());
        
        JScrollPane listScroll = new JScrollPane(reservationList);
        listScroll.setPreferredSize(new Dimension(300, 200));
        
        // Details area
        txtDetails = new JTextArea();
        txtDetails.setEditable(false);
        txtDetails.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtDetails.setBackground(new Color(250, 250, 250));
        JScrollPane detailsScroll = new JScrollPane(txtDetails);
        
        // Buttons for list actions
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        actionPanel. setBackground(Color. WHITE);
        
        JButton btnPay = new JButton("Process Payment");
        btnPay.setBackground(successColor);
        btnPay.setForeground(Color.WHITE);
        btnPay.addActionListener(e -> processSelectedPayment());
        reservationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String value = reservationList.getSelectedValue();
                if (value != null && value.contains("Paid")) {
                    btnPay.setText("Paid");
                } else {
                    btnPay.setText("Process Payment");
                }
            
            }
        });
        
        
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBackground(accentColor);
        btnDelete.setForeground(Color. WHITE);
        btnDelete.addActionListener(e -> {
            deleteSelected();
            updateStatusBar();
        });
        
        JButton btnReceipt = new JButton("Receipt");
        // btnRefresh.addActionListener(e -> refreshReservationList());
        btnReceipt.addActionListener(e -> {
            int index = reservationList.getSelectedIndex();
            if (index < 0) {
                JOptionPane.showMessageDialog(this, "Please select a reservation first.");
                return;
            }
            String item = reservationList.getSelectedValue();
            String bookingId = (item != null && item.length() >= 12) ? item.substring(0, 12).trim() : "";
            showReceipt(ReservationManager.findByID(bookingId));

        });

        JButton btnHousekeeping = new JButton("Housekeeping");
        btnHousekeeping.setBackground(cleaningColor);
        btnHousekeeping.setForeground(Color.WHITE);
        btnHousekeeping.addActionListener(e -> openHousekeepingDialog());
        
        JButton btnStatus = new JButton("Set Status");
        btnStatus.setBackground(warningColor);
        btnStatus.setForeground(Color.WHITE);
        btnStatus.addActionListener(e -> changeRoomStatus());
        
        JButton btnServices = new JButton("Services");
        btnServices.addActionListener(e -> viewGuestServices());
        
        actionPanel.add(btnPay);
        actionPanel.add(btnDelete);
        actionPanel.add(btnReceipt);
        actionPanel.add(btnStatus);
        actionPanel.add(btnHousekeeping);
        actionPanel.add(btnServices);
        
        // Split between list and details
        JSplitPane innerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,listScroll, detailsScroll);
        innerSplit.setDividerLocation(120);
        
        panel.add(innerSplit, BorderLayout.CENTER);
        panel.add(actionPanel, BorderLayout. SOUTH);
        
        return panel;
    }

    private void switchCardPanel() {
        String selectedType = (String) cmbRoomType.getSelectedItem();
        cardLayout.show(cardPanel, selectedType);
        txtRate.setText("");
    }

    //add Reservation
    private void addReservation() {
        try {
            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();
            LocalDate checkInDate = LocalDate.parse(txtCheckIn.getText().trim());
            int nights = Integer.parseInt(txtNights.getText().trim());
            double rate = Double.parseDouble(txtRate.getText().trim());

            String rooomType = (String) cmbRoomType.getSelectedItem();
            Reservation reservation = null;

            switch (rooomType) {
                case "Standard Room":
                    int roomNumber = Integer.parseInt(txtRoomNumber.getText().trim());
                    String bedType = (String) cmbBedType.getSelectedItem();
                    boolean hasWifi = chkWifi.isSelected();

                    reservation = new StandardRoom(name, contact, checkInDate, nights, rate, roomNumber, bedType, hasWifi);
                    break;
                case "Suite Room":
                    int level = Integer.parseInt(txtLevel.getText().trim());
                    String suiteType = (String) cmbSuiteType.getSelectedItem();
                    boolean hasJacuzzi = chkJacuzzi.isSelected();

                    reservation = new SuiteRoom(name, contact, checkInDate, nights, rate, level, suiteType, hasJacuzzi);
                    break;
                case "Conference Hall":
                    String hallName = (String) cmbHallName.getSelectedItem();
                    int capacity = Integer.parseInt(txtCapacity.getText().trim());
                    boolean hasProjector = chkProjector.isSelected();

                    reservation = new ConferenceHall(name, contact, checkInDate, nights, rate, hallName, capacity, hasProjector);
                    break;
            }

            if (reservation != null) {
                ReservationManager.addReservation(reservation);
                refreshReservationList();
                clearInputFields();
                JOptionPane.showMessageDialog(this, "Reservation added successfully!\nBookingID: " + reservation.getBookingID(),"Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number format: " + ex.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidReservationException ex) {
            JOptionPane.showMessageDialog(this, "Error adding reservation: " + ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
        }
    }

    //Refresh
    private void refreshReservationList() {
        listModel.clear();

        for (Reservation r : ReservationManager.getAllReservations()) {
            String status = r.getIsPaid() ? "Paid" : "Unpaid";
            String type = "";

            if (r instanceof StandardRoom) {
                type = "Standard";
            } else if (r instanceof SuiteRoom) {
                type = "Suite";
            } else if (r instanceof ConferenceHall) {
                type = "Conference";
            }
            listModel.addElement(r.getBookingID() + " " + type + " - " + r.getClientDetails().getName() + " | " + status);

        }
    }

    //show selected r details
    private void showSelectedDetails() {
        int index = reservationList.getSelectedIndex();
        if (index >= 0 && index < ReservationManager.getAllReservations().size()) {
            Reservation r = ReservationManager.getAllReservations().get(index);
            
            StringBuilder details = new StringBuilder();
            details.append(r.getConfirmationDetails());
            details.append("\n");

            details.append("\n--- Guest Services  ---\n");
            details.append(r.getGuestServicesAsString());
            
            details.append("\n--- Housekeeping Notes ---\n");
            details.append(r.getHousekeepingNotesAsString());

            Reservation.Reminder reminder = r.new Reminder();
            details.append("Reminder Status: ").append(reminder.checkReminder()).append("\n");

            txtDetails.setText(details.toString());
            txtDetails.setCaretPosition(0);

            if (reminder.checkReminder().contains("OVERDUE")) {
                txtDetails.setBackground(accentColor);
                txtDetails.setForeground(Color.WHITE);
            } else {
                txtDetails.setBackground(new Color(250, 250, 250));
                txtDetails.setForeground(Color.BLACK);
            }
        }
    }

    //process payment
    private void processSelectedPayment() {
        int index = reservationList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation");
            return;
        }

        Reservation r = ReservationManager.getAllReservations().get(index);

        if (r.getIsPaid()) {
            JOptionPane.showMessageDialog(this, "This reservation already be paid");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Process payment for \n Booking " + r.getBookingID() + " - RM" + r.calculateTotal(), "Confirm Payment", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            r.applyDiscount();
            r.processPayment();
            refreshReservationList();
            showSelectedDetails();
            JOptionPane.showMessageDialog(this, "Payment success");
        }
    }

    //delected r
    private void deleteSelected() {
        int index = reservationList.getSelectedIndex();

        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation");
            return;
        }
        Reservation r = ReservationManager.getAllReservations().get(index);

        int confirm = JOptionPane.showConfirmDialog(this, "Delete Reservation? ", "Confirm Delete",JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            ReservationManager.removeReservation(r.getBookingID().getId());
            refreshReservationList();
            updateStatusBar();
            txtDetails.setText("");
        }
    }

    //housekeeping dialog
    private void openHousekeepingDialog() {
        int index = reservationList.getSelectedIndex();
        String item = reservationList.getSelectedValue();

        if (item.contains("Conference")) {
            JOptionPane.showMessageDialog(this, 
                "Housekeeping notes are not applicable for Conference Hall bookings.",
                "Not Applicable", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation first.");
            return;
        }
        
        Reservation r = ReservationManager.getAllReservations().get(index);
        
        // Create dialog
        JDialog dialog = new JDialog(this, "Housekeeping Notes - " + r.getBookingID(), true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        // Existing notes
        JTextArea notesArea = new JTextArea(r.getHousekeepingNotesAsString());
        notesArea.setEditable(false);
        notesArea.setFont(new Font("Monospaced", Font. PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(notesArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Existing Notes"));
        
        // Add new note
        JPanel addPanel = new JPanel(new BorderLayout(5, 5));
        addPanel.setBorder(BorderFactory.createTitledBorder("Add New Note"));
        
        JTextField txtNewNote = new JTextField();
        JButton btnAdd = new JButton("Add Note");
        btnAdd.setBackground(successColor);
        btnAdd.setForeground(Color.WHITE);
        
        btnAdd.addActionListener(e -> {
            String note = txtNewNote.getText().trim();
            if (!note.isEmpty()) {
                r.addHousekeepingNote(note);
                notesArea.setText(r.getHousekeepingNotesAsString());
                txtNewNote.setText("");
                showSelectedDetails();
            }
        });
        
        addPanel.add(txtNewNote, BorderLayout.CENTER);
        addPanel.add(btnAdd, BorderLayout.EAST);
        
        // Quick notes buttons
        JPanel quickPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quickPanel.setBorder(BorderFactory. createTitledBorder("Quick Notes"));
        quickPanel.setPreferredSize(new Dimension(0,90));
        
        String[] quickNotes = {"Room cleaned", "Towels replaced", "Minibar restocked", 
                               "Maintenance needed", "Guest complaint"};
        for (String qn : quickNotes) {
            JButton btn = new JButton(qn);
            btn.setFont(new Font("Arial", Font.PLAIN, 10));
            btn.addActionListener(e -> {
                r.addHousekeepingNote(qn);
                notesArea.setText(r.getHousekeepingNotesAsString());
                showSelectedDetails();
            });
            quickPanel.add(btn);
        }
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel. add(quickPanel, BorderLayout. NORTH);
        bottomPanel.add(addPanel, BorderLayout. SOUTH);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(bottomPanel, BorderLayout. SOUTH);
        
        dialog.setVisible(true);
    }

    //change room status
    private void changeRoomStatus() {
        int index = reservationList.getSelectedIndex();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, "Please select a reservation");
            return;
        }

        Reservation r = ReservationManager.getAllReservations().get(index);

        String[] statuses = {"AVAILABLE", "OCCUPIED", "CLEANING", "MAINTENANCE"};
        String newStatus = (String) JOptionPane.showInputDialog(this, "Select new room status:", "Change Room Status", JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);

        if (newStatus != null && !newStatus.isEmpty()) {
            r.setRoomStatus(newStatus);
            showSelectedDetails();
            JOptionPane.showMessageDialog(this, "Room status updated to " + newStatus);
            updateStatusBar();
        }
    }

    //order guest service
    private void orderGuestService(String service) {
        int index = reservationList.getSelectedIndex();
        String item = reservationList.getSelectedValue();
        if (index < 0) {
            JOptionPane.showMessageDialog(this, 
                "Please select a reservation first to order services.",
                "No Reservation Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (item.contains("Paid")) {
            JOptionPane.showMessageDialog(this, 
                "Cannot order services for a paid reservation.",
                "Reservation Paid", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (item.contains("Conference")) {
            JOptionPane.showMessageDialog(this, 
                "Guest services are not available for Conference Hall bookings.",
                "Service Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }

        
        Reservation r = ReservationManager.getAllReservations().get(index);

        double price = getServicePrice(service);
        String priceText = (price == 0.0) ? "This service is FREE." : String.format("Cost: RM%.2f", price);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Order '" + service + "' for " + r.getClientDetails().getName() + "?\n" +
            priceText,
            "Confirm Service", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            r.addGuestService(service);
            showSelectedDetails();
            JOptionPane.showMessageDialog(this, 
                service + " has been ordered! ",
                "Service Ordered", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Returns the price for each guest service
    private double getServicePrice(String service) {
        switch (service) {
            case "Room Service":
                return 25.0;
            case "Laundry":
                return 15.0;
            case "Spa":
                return 100.0;
            case "Wake-up Call":
                return 0.0;
            case "Transportation":
                return 50.0;
            default:
                return 0.0;
        }
    }

    //view guest services
    private void viewGuestServices() {
        int index = reservationList.getSelectedIndex();
        if (index < 0) {
            JOptionPane. showMessageDialog(this, "Please select a reservation first.");
            return;
        }
        
        Reservation r = ReservationManager.getAllReservations().get(index);
        
        StringBuilder sb = new StringBuilder();
        sb.append("Guest Services for:  ").append(r.getClientDetails().getName()).append("\n");
        sb.append("Booking:  ").append(r.getBookingID()).append("\n\n");
        sb.append(r.getGuestServicesAsString());
        sb.append("\n─────────────────────\n");
        sb.append("Services Total: RM").append(r.calculateServices());
        
        JOptionPane.showMessageDialog(this, sb. toString(), 
                                      "Guest Services", JOptionPane. INFORMATION_MESSAGE);
    }

    //show status board dialog
    private void showStatusBoardDialog() {
        JDialog dialog = new JDialog(this, "Room Status Board", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        String[] statuses = {"AVAILABLE", "OCCUPIED", "CLEANING", "MAINTENANCE"};
        Color[] colors = {successColor, accentColor, cleaningColor, warningColor};
        
        for (int i = 0; i < statuses. length; i++) {
            JPanel panel = new JPanel(new BorderLayout());
            DefaultListModel<String> model = new DefaultListModel<>();
            
            for (Reservation r : ReservationManager.getReservationsByStatus(statuses[i])) {
                String type = "";
                if (r instanceof StandardRoom) type = "[STD Room " + ((StandardRoom)r).getRoomNumber() + "]";
                else if (r instanceof SuiteRoom) type = "[Suite Floor " + ((SuiteRoom)r).getLevel() + "]";
                else if (r instanceof ConferenceHall) type = "[Hall:  " + ((ConferenceHall)r).getHallName() + "]";
                
                model.addElement(r.getBookingID() + " " + type + " - " + r.getClientDetails().getName());
            }
            
            JList<String> list = new JList<>(model);
            list.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JLabel countLabel = new JLabel("  Total: " + model. size() + " rooms", SwingConstants.CENTER);
            countLabel.setFont(new Font("Arial", Font.BOLD, 14));
            countLabel.setForeground(colors[i]);
            
            panel.add(new JScrollPane(list), BorderLayout.CENTER);
            panel.add(countLabel, BorderLayout. SOUTH);
            
            tabbedPane. addTab(statuses[i] + " (" + model.size() + ")", panel);
        }
        
        dialog.add(tabbedPane, BorderLayout.CENTER);
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel. add(btnClose);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    //clear all text field
    private void clearInputFields() {
        txtName.setText("");
        txtContact.setText("");
        txtCheckIn. setText("");
        txtNights.setText("");
        txtRate. setText("");
        txtRoomNumber.setText("");
        txtLevel.setText("");
        // txtHallName.setText("");
        // txtCapacity.setText("");
        chkWifi.setSelected(false);
        chkJacuzzi.setSelected(false);
        // chkProjector.setSelected(false);
    }

    //show report
    private void showReport() {
        JTextArea reportArea = new JTextArea(ReservationManager.generateReport());
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Reservation Report", JOptionPane.INFORMATION_MESSAGE);
    }

    //show about
    private void showAbout() {
        String about = "Smart Hospitality System (SHS)\n\n" +
                       "Version 1.0\n" +
                       "Hotel Room Management System\n\n" +
                       "Developed for: ISB16003 - Object Oriented Programming\n" +
                       "Theme: Hotel Room Management\n\n" +
                       "Group Members:\n" +
                       "1. Mohamad Faiz Fahmi\n" +
                       "2. [Member 2]\n" +
                       "3. [Member 3]\n" +
                       "4. [Member 4]";
        
        JOptionPane.showMessageDialog(this, about, "About Us", JOptionPane.INFORMATION_MESSAGE);

    }

    //receipt
    private void showReceipt(Reservation r) {
        StringBuilder receipt = new StringBuilder();
        receipt.append("------- Hotel Receipt -------\n");
        receipt.append("Booking ID: ").append(r.getBookingID()).append("\n");
        receipt.append("Guest Name: ").append(r.getClientDetails().getName()).append("\n");
        receipt.append("Contact Info: ").append(r.getClientDetails().getContact()).append("\n");
        receipt.append("Check-in Date: ").append(r.getcheckInDate()).append("\n");
        receipt.append("Nights Stayed: ").append(r.getNights()).append("\n");
        receipt.append("Room Rate: RM").append(String.format("%.2f", r.getRoomRate())).append("\n");
        receipt.append("Reservation Type: ");
        if (r instanceof StandardRoom) {
            receipt.append("Standard Room\n");
        } else if (r instanceof SuiteRoom) {
            receipt.append("Suite Room\n");
        } else if (r instanceof ConferenceHall) {
            receipt.append("Conference Hall\n");
        } else {
            receipt.append("Unknown Type\n");
        }

        receipt.append("\n----- Charges -----\n");
        receipt.append("Room Charges: RM").append(String.format("%.2f", (r.getNights() * r.getRoomRate()))).append("\n");
        if (r instanceof StandardRoom && ((StandardRoom) r).getHasWifi()) {
            receipt.append("Wifi Charges: RM").append(String.format("%.2f", (r.getNights() * 10.0))).append("\n");
        } else if (r instanceof StandardRoom) {
            receipt.append("Wifi Charges: RM0.00\n");
        }
        if (r instanceof SuiteRoom && ((SuiteRoom) r).getHasJacuzzi()) {
            receipt.append("Jacuzzi Charges: RM").append(String.format("%.2f", (r.getNights() * 75.0))).append("\n");
        } else if (r instanceof SuiteRoom) {
            receipt.append("Jacuzzi Charges: RM0.00\n");
        }
        if (r instanceof ConferenceHall && ((ConferenceHall) r).getHasProjector()) {
            receipt.append("Projector Charges: RM").append(String.format("%.2f", (r.getNights() * 100.0))).append("\n");
        } else if (r instanceof ConferenceHall) {
            receipt.append("Projector Charges: RM0.00\n");
        }
        receipt.append("Service Charges: RM").append(String.format("%.2f", r.calculateServices())).append("\n");
        receipt.append("-------------------\n");
        receipt.append("Total Amount: RM").append(String.format("%.2f", r.calculateTotal())).append("\n");
        receipt.append("-------------------\n");
        receipt.append("Payment Status: ").append(r.getIsPaid() ? "PAID" : "UNPAID").append("\n");
        receipt.append("-------------------\n");
        receipt.append("Thank you for staying with us!\n");

        JTextArea receiptArea = new JTextArea(receipt.toString());
        receiptArea.setEditable(false);
        receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(receiptArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelGUI());
    }

}
