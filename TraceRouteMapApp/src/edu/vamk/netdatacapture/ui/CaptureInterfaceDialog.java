package edu.vamk.netdatacapture.ui;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

@SuppressWarnings("serial")
public class CaptureInterfaceDialog extends JDialog{
	
	public CaptureInterfaceDialog(Frame owner, boolean modility, String title, final CaptureInterfaceDialogCallBack callback){
		super(owner,title, modility);
		
		JPanel jp= new JPanel();
		GridLayout gl= new GridLayout(0, 2);
		jp.setLayout(gl);
		
		NetworkInterface[] devicelist = JpcapCaptor.getDeviceList();

		for(int i=0;i<devicelist.length;i++){
			NetworkInterface device=devicelist[i];
			System.out.println(device.name);
			JLabel jl= new JLabel(device.name);
			jp.add(jl);
			
			JButton jb= new JButton("Start");
			final int retVal=i;
			jb.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					callback.selectInterfaceDeviceNumber(retVal);
					CaptureInterfaceDialog.this.dispose();
				}
			});
			jp.add(jb);
			NetworkInterfaceAddress[] addresses=device.addresses;
			for(NetworkInterfaceAddress addresse: addresses){
				System.out.println("addresses: "+addresse.address.getHostAddress());
			}
		
		}
		
		getContentPane().add(jp);
	
	}
	
	public interface CaptureInterfaceDialogCallBack{
		
		public void selectInterfaceDeviceNumber(int val);
		
	}

}
