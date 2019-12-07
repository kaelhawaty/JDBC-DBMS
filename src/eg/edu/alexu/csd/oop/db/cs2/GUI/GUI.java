package eg.edu.alexu.csd.oop.db.cs2.GUI;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;

import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class GUI extends JFrame {

    private JTable table;
    private JLabel titleLabel, pathLabel, timeLabel, numberLabel;
    private JTextArea textField;
    private JTabbedPane tabbedPane;
    private JButton executeButton, pathButton, timeButton;
    private JScrollPane textScroll, statusScroll, tableScroll, loggerScroll;
    private String currentPath = new File("").getAbsolutePath();
    private Controller controller;
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    GUI frame = new GUI();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public GUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(0, 0, 1000, 1000);
        setTitle("JDBC");
        titleLabel = new JLabel("JDBC Project");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 35));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setBounds(12, 13, 259, 86);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        textField = new JTextArea();
        textField.setColumns(10);
        textScroll = new JScrollPane(textField);
        textScroll.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        pathLabel = new JLabel("Current Path");
        pathLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));

        executeButton = new JButton("Execute");
        pathButton = new JButton("PATH");
        ActionListener buttonListeners = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(pathButton)){
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int r = fileChooser.showDialog(null, "Select");
                    if(r == JFileChooser.APPROVE_OPTION){
                        currentPath = fileChooser.getSelectedFile().getAbsolutePath();
                        pathLabel.setText(currentPath);
                        controller.setConnection(currentPath);
                    }
                }else if (e.getSource().equals(executeButton)){
                    String []queries = textField.getText().split("\\n");
                    if(queries.length==1 && queries[0].equals(""))
                        JOptionPane.showMessageDialog(getContentPane(), "The text area is empty, Please enter your SQL statement");
                    for (String query : queries){
                        controller.execute(query);
                    }
                }else {
                    String timeInString = JOptionPane.showInputDialog("Enter a valid time in seconds");
                    if(!timeInString.matches("[0-9]+"))
                        JOptionPane.showMessageDialog(getContentPane(),"Wrong input");
                    else {
                        controller.setQueryTimeOut(Integer.parseInt(timeInString));
                        numberLabel.setText(timeInString);
                    }
                }
            }
        };
        pathButton.addActionListener(buttonListeners);
        executeButton.addActionListener(buttonListeners);

        timeButton = new JButton("Change");
        timeButton.addActionListener(buttonListeners);
        numberLabel = new JLabel("100");
        numberLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
        timeLabel = new JLabel("Query Time Out");

        GroupLayout groupLayout = initializeLayout();
        JTextArea statusArea = new JTextArea();
        statusArea.setEditable(false);
        statusScroll = new JScrollPane(statusArea);
        statusScroll.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("Status", null, statusScroll, null);

        JTextArea loggerArea = new JTextArea(5, 20);
        loggerArea.setEditable(false);
        loggerScroll = new JScrollPane(loggerArea);
        loggerScroll.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("Logger", null, loggerScroll, null);
        table = new JTable();
        table.setEnabled(false);
        tableScroll = new JScrollPane(table);
        tableScroll.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        tabbedPane.addTab("Table", null, tableScroll, null);

        getContentPane().setLayout(groupLayout);
        controller = new Controller(currentPath, tabbedPane.getComponents());


    }
    private GroupLayout initializeLayout(){
        GroupLayout groupLayout = new GroupLayout(getContentPane());
        groupLayout.setHorizontalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addComponent(tabbedPane, GroupLayout.DEFAULT_SIZE, 970, Short.MAX_VALUE)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(titleLabel)
                                                .addPreferredGap(ComponentPlacement.RELATED, 400, Short.MAX_VALUE)
                                                .addComponent(pathLabel, GroupLayout.PREFERRED_SIZE, 329, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(139)
                                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                                        .addComponent(executeButton)
                                                        .addComponent(textScroll, GroupLayout.PREFERRED_SIZE, 496, GroupLayout.PREFERRED_SIZE))
                                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addPreferredGap(ComponentPlacement.RELATED, 188, Short.MAX_VALUE)
                                                                .addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
                                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                                .addComponent(numberLabel)
                                                                                .addGap(97))
                                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                                .addComponent(timeButton)
                                                                                .addGap(72))))
                                                        .addGroup(groupLayout.createSequentialGroup()
                                                                .addGap(43)
                                                                .addComponent(pathButton))
                                                        .addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
                                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                                .addComponent(timeLabel, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
                                                                .addGap(13)))))
                                .addContainerGap())
        );
        groupLayout.setVerticalGroup(
                groupLayout.createParallelGroup(Alignment.LEADING)
                        .addGroup(groupLayout.createSequentialGroup()
                                .addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addContainerGap()
                                                .addComponent(titleLabel)
                                                .addGap(18)
                                                .addComponent(textScroll, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(29)
                                                .addComponent(pathLabel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(pathButton)
                                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(timeLabel)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(numberLabel)))
                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(executeButton)
                                        .addComponent(timeButton))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 781, GroupLayout.PREFERRED_SIZE))
        );

        return groupLayout;
    }
}
