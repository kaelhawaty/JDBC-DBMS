package eg.edu.alexu.csd.oop.db.cs2.GUI;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    private JTable table;
    private JLabel titleLabel, pathLabel;
    private JTextField textField;
    private JTabbedPane tabbedPane;
    private JButton executeButton, pathButton;

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
        ActionListener buttonListeners = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource().equals(pathLabel)){
                    JFileChooser fileChooser = new JFileChooser();
                    
                }else{

                }
            }
        }
        titleLabel = new JLabel("JDBC Project");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 35));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setBounds(12, 13, 259, 86);

        tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        textField = new JTextField();
        textField.setColumns(10);

        pathLabel = new JLabel("Current Path");
        pathLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));

        executeButton = new JButton("Execute");

        pathButton = new JButton("PATH");
        GroupLayout groupLayout = initializeLayout();
        JTextArea statusArea = new JTextArea();
        tabbedPane.addTab("Status", null, statusArea, null);

        JTextArea loggerArea = new JTextArea();
        tabbedPane.addTab("Logger", null, loggerArea, null);

        table = new JTable();
        tabbedPane.addTab("Table", null, table, null);
        getContentPane().setLayout(groupLayout);
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
                                                        .addComponent(textField, GroupLayout.PREFERRED_SIZE, 496, GroupLayout.PREFERRED_SIZE))
                                                .addGap(111)
                                                .addComponent(pathButton)))
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
                                                .addComponent(textField, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
                                        .addGroup(groupLayout.createSequentialGroup()
                                                .addGap(40)
                                                .addComponent(pathLabel, GroupLayout.PREFERRED_SIZE, 27, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(ComponentPlacement.RELATED)
                                                .addComponent(pathButton)))
                                .addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(executeButton)
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(tabbedPane, GroupLayout.PREFERRED_SIZE, 781, GroupLayout.PREFERRED_SIZE))
        );
        return groupLayout;
    }
}