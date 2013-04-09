package edu.vamk.netdatacapture.ui;

//Usually you will require both swing and awt packages
// even if you are working with just swings.
import javax.swing.*;

import edu.vamk.netdatacapture.TraceRouteMapDraw;
import edu.vamk.netdatacapture.component.NetNode;
import edu.vamk.netdatacapture.component.RouteMapPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class TraceRouteWindow {
	
	public static int SELECTEDDEVICENUMBER=0;
	public static final int LABELSIZE = 80;
	public static final String LABELSTYLE = "<html><p align=\"center\" style=\"width:%dpx;\">%s<p><html>";
	static JTextField tf_domain_name;
	static JPanel panel;
	static RouteMapPanel traceMap_panel;
	static ButtonGroup buttonGroup;
	static String showType = "IP";

	public static void main(String args[]) {
		// Creating the Frame
		final JFrame frame = new JFrame("TraceRoute Frame");
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		// Creating the MenuBar and adding components
		JMenuBar mb = new JMenuBar();
		JMenuItem m1 = new JMenuItem("Capture Interface");
		m1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("myDialog");
				// TODO Auto-generated method stub
				CaptureInterfaceDialog myDialog = new CaptureInterfaceDialog(
						frame,
						true,
						"Please select a device?",
						new CaptureInterfaceDialog.CaptureInterfaceDialogCallBack() {

							@Override
							public void selectInterfaceDeviceNumber(int val) {
								SELECTEDDEVICENUMBER=val;

							}
						});
				myDialog.setPreferredSize(new Dimension(300, 150));
				myDialog.pack();
				myDialog.setLocationRelativeTo(frame);
				myDialog.setVisible(true);

			}
		});
		mb.add(m1);

		// Creating the panel at bottom and adding components
		panel = new JPanel(); // the panel is not visible in output
		JLabel label = new JLabel("Trace route to");
		tf_domain_name = new JTextField(10);// accepts upto 10 characters
		JButton send = new JButton("Send");
		JButton reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tf_domain_name.setText("");
				traceMap_panel.validate();
				for (Map.Entry<Short, List<NetNode>> entry : traceMap_panel
						.getNodeMap().entrySet()) {
					List<NetNode> nodes = entry.getValue();
					for (NetNode n : nodes) {
						n.setLocation(n.getPosPoint());
						System.out.println(n.getLocation());
					}
				}
				traceMap_panel.repaint();
			}
		});

		try {
			InetAddress giriAddress = java.net.InetAddress
					.getByName("69.59.196.211");
			System.out.println(giriAddress);
			InetAddress ga = java.net.InetAddress
					.getByName("stackoverflow.com");
			System.out.println(ga);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		final JRadioButton jb_name = new JRadioButton("Name");
		final JRadioButton jb_ip = new JRadioButton("IP");

		panel.add(label);// Components Added using Flow Layout
		panel.add(tf_domain_name);
		panel.add(send);
		panel.add(reset);
		panel.add(jb_name);
		panel.add(jb_ip);

		jb_ip.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (jb_ip.isSelected()) {
					jb_name.setSelected(false);
					showType = "IP";
					System.out.println("jb_ip");
					mapPanelRefresh(showType);
				}
			}
		});
		jb_name.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (jb_name.isSelected()) {
					jb_ip.setSelected(false);
					showType = "Name";
					System.out.println("jb_name");
					mapPanelRefresh(showType);
					sizeChanged();
				}
			}
		});
		// Text Area at the Center
		traceMap_panel = new RouteMapPanel();
		traceMap_panel.setForeground(Color.WHITE);
		traceMap_panel.setBackground(Color.WHITE);

		final JScrollPane scrollPanel = new JScrollPane(traceMap_panel);

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String dstValue = tf_domain_name.getText();
				TraceRouteMapDraw tw = new TraceRouteMapDraw(traceMap_panel,
						dstValue, showType);
				new Thread(tw).start();
			}
		});
		// Adding Components to the frame.
		frame.getContentPane().add(BorderLayout.SOUTH, panel);
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.getContentPane().add(BorderLayout.CENTER, scrollPanel);
		frame.setVisible(true);
	}

	public static void sizeChanged() {
		traceMap_panel.revalidate();
		traceMap_panel.refreshPanelSize();
		for (Map.Entry<Short, List<NetNode>> entry : traceMap_panel
				.getNodeMap().entrySet()) {
			List<NetNode> nodes = entry.getValue();
			for (NetNode n : nodes) {
				if (showType.equals("Name")) {
					String labelText = String
							.format(TraceRouteWindow.LABELSTYLE, LABELSIZE,
									n.getName());
					n.setText(labelText);
					n.setPosPoint(new Point(n.getPosPoint().x,
							n.getPosPoint().y));

				} else {
					String labelText = String.format(
							TraceRouteWindow.LABELSTYLE, LABELSIZE,
							n.getSrcAddress());
					n.setText(labelText);
				}
				n.setBounds(n.getPosPoint().x, n.getPosPoint().y,
						n.getPreferredSize().width, n.getPreferredSize().height);
			}

			traceMap_panel.repaint();
		}

	}

	public static void mapPanelRefresh(String showType) {
		traceMap_panel.revalidate();
		traceMap_panel.refreshPanelSize();

		for (Map.Entry<Short, List<NetNode>> entry : traceMap_panel
				.getNodeMap().entrySet()) {
			List<NetNode> nodes = entry.getValue();
			for (NetNode n : nodes) {
				if (showType.equals("Name")) {
					int preWidth = n.getPreferredSize().width;
					String labelText = String
							.format(TraceRouteWindow.LABELSTYLE, LABELSIZE,
									n.getName());
					n.setText(labelText);

				} else {
					String labelText = String.format(
							TraceRouteWindow.LABELSTYLE, LABELSIZE,
							n.getSrcAddress());
					n.setText(labelText);
				}
				n.setBounds(n.getPosPoint().x, n.getPosPoint().y,
						n.getPreferredSize().width, n.getPreferredSize().height);
			}

			traceMap_panel.repaint();
		}

	}
}