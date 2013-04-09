package edu.vamk.netdatacapture;

import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.EthernetPacket;
import jpcap.packet.ICMPPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.Packet;
import edu.vamk.netdatacapture.component.NetNode;
import edu.vamk.netdatacapture.component.Node;
import edu.vamk.netdatacapture.component.RouteMapPanel;
import edu.vamk.netdatacapture.component.Tree;
import edu.vamk.netdatacapture.ui.TraceRouteWindow;

public class TraceRouteMapDraw implements Runnable , ComponentListener{
	
	private RouteMapPanel mapPanel;
	private String dstName;
	private boolean test=false;
	private boolean isDestination=false;
	boolean getGwmac=true;
	private Node<NetNode> latestNode;
	private String showType;
	List<NetNode> routeList;
	
	private boolean timeOutTimeAtFirstTime=true;
	
	private String firstHop="";
	private int firstHopPos=0;

//	Map<MapNodeMarker,NetNode> nodes_map = new HashMap<MapNodeMarker,NetNode>();
/**
 * 
 * @param mapPanel
 * @param dstName
 * @param showType set label text show type is Name or Ip address
 */
	public TraceRouteMapDraw(RouteMapPanel mapPanel,String dstName,String showType){
		
		this.showType=showType;
		this.mapPanel=mapPanel;
		mapPanel.addComponentListener(this);
		this.dstName= dstName;
		Tree<NetNode> tree= mapPanel.getRouteMapTree();
		//get Root Element
		latestNode = tree.getRootElement();
		routeList= new ArrayList<NetNode>();
	}
	
	@Override
	public void run(){
		try {
		if(!test){
			
			System.out.println("DEVICE LIST________________________________________________");
			for(NetworkInterface device:JpcapCaptor.getDeviceList()){
				
				System.out.println(device.name);
				NetworkInterfaceAddress[] addresses=device.addresses;
				for(NetworkInterfaceAddress addresse: addresses){
					System.out.println("addresses: "+addresse.address.getHostAddress());
				}
			
			}
			System.out.println("DEVICE LIST________________________________________________");
			
			

			
			
			NetworkInterface device=JpcapCaptor.getDeviceList()[TraceRouteWindow.SELECTEDDEVICENUMBER];
			JpcapCaptor captor = null;
		try {
			captor = JpcapCaptor.openDevice(device,5000,false,10000);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		InetAddress thisIP=null;
		for(NetworkInterfaceAddress addr:device.addresses)
			if(addr.address instanceof Inet4Address){
				thisIP=addr.address;
				break;
			}
		//obtain MAC address of the default gateway
		InetAddress pingAddr=InetAddress.getByName("www.google.fi");
		captor.setFilter("tcp and dst host "+pingAddr.getHostAddress(),true);
		byte[] gwmac=null;
		while(getGwmac){
			new URL("http://www.google.fi").openStream().close();
			Packet ping=captor.getPacket();
			if(ping==null){
				System.exit(-1);
			}else if(Arrays.equals(((EthernetPacket)ping.datalink).dst_mac,device.mac_address))
					continue;
			gwmac=((EthernetPacket)ping.datalink).dst_mac;
			break;
		}
		ICMPPacket icmp=new ICMPPacket();
		icmp.type=ICMPPacket.ICMP_ECHO;
		icmp.seq=1;
		icmp.id=0;
		// create traceroute trace to google.fi
			icmp.setIPv4Parameter(0,false,false,false,0,false,false,false,0,0,0,IPPacket.IPPROTO_ICMP,
					thisIP,InetAddress.getByName(dstName));
		
		icmp.data="data".getBytes();
		
		
		EthernetPacket ether=new EthernetPacket();
		ether.frametype=EthernetPacket.ETHERTYPE_IP;
		ether.src_mac=device.mac_address;
		ether.dst_mac=gwmac;
		icmp.datalink=ether;
		
			captor.setFilter("icmp and dst host "+thisIP.getHostAddress(),true);
		JpcapSender sender=captor.getJpcapSenderInstance();
		sender.sendPacket(icmp);
		
		
		while(!isDestination&&icmp.hop_limit<30){
			
			boolean isRouteExist=false;
			ICMPPacket p=(ICMPPacket) captor.getPacket();
			
			if(p==null){
				if(timeOutTimeAtFirstTime){
				System.out.println("Timeout");
				timeOutTimeAtFirstTime=false;
				}else{
					addNewNodeOnRouteMap(icmp, p, "/img/sair.png");
					icmp.hop_limit++;
				}
			}else if(p.type==ICMPPacket.ICMP_TIMXCEED){
				System.out.println(p.toString());
				System.out.println(p.alive_time);
				
				//from 0 start get first ip && hop_limit 0 -1  255
				if(icmp.hop_limit==0){
					firstHop=p.src_ip.getHostAddress();
					addNewNodeOnRouteMap(icmp, p, "/img/router.gif");
					icmp.hop_limit++;
				}
				//get last first ip hop number
				if(p.src_ip.getHostAddress().equals(firstHop)){
					firstHopPos++;
					icmp.hop_limit++;
				}else{
				System.out.println("hoplimit" + icmp.hop_limit);
				addNewNodeOnRouteMap(icmp, p, "/img/router.gif");
				icmp.hop_limit++;
				}
				
			}else if(p.type==ICMPPacket.ICMP_UNREACH){
				p.src_ip.getHostName();
//				System.out.println(icmp.hop_limit+": "+p.src_ip +" unreach");
				System.exit(0);
			}else if(p.type==ICMPPacket.ICMP_ECHOREPLY){
				addNewNodeOnRouteMap(icmp, p, "/img/no.png");
				isDestination=true;
			}else continue;
			icmp.seq++;
			sender.sendPacket(icmp);
//			Thread.sleep(1000);
		}
		}
		} catch (Exception e) {
e.printStackTrace();
}
	}

	
	public void addNewNodeOnRouteMap(ICMPPacket icmp,ICMPPacket p,String imgSrc){
		
		ImageIcon node = new ImageIcon(getClass().getResource(imgSrc));
		NetNode node_label  = new NetNode(node);
		if(p!=null)
		node_label.setSrcAddress(p.src_ip.getHostAddress());
		else
		node_label.setSrcAddress("timeout!");
			
		try {
			if(node_label.getName()!=null&&!node_label.getName().equals("local pc"));
			if(!node_label.getSrcAddress().equals("timeout!"))
			node_label.setName(CapUtil.reverseDns(node_label.getSrcAddress()));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String labelText = String.format(TraceRouteWindow.LABELSTYLE, TraceRouteWindow.LABELSIZE,node_label.getSrcAddress());
		node_label.setText(labelText);
		node_label.setHorizontalTextPosition(JLabel.CENTER);
		node_label.setVerticalTextPosition(JLabel.BOTTOM);
		if(p!=null)
			node_label.setInNetAddr(p.src_ip);
		else
			node_label.setInNetAddr(null);
			
		node_label.setBounds(0, 0, node_label.getPreferredSize().width, node_label.getPreferredSize().height);
		Point nodePosPoint= new Point((icmp.hop_limit+1-firstHopPos)*90,10);
		node_label.setPosPoint(nodePosPoint);
		Node<NetNode> newNode = null;
		if(mapPanel.addNodeToMap((short) (icmp.hop_limit+1-firstHopPos), node_label)){
			mapPanel.add(node_label);
			MouseDragPicListener listener= new MouseDragPicListener(node_label);
			node_label.addMouseListener(listener);
			node_label.addMouseMotionListener(listener);
			node_label.setToolTipText(node_label.getName());
			newNode= new Node<NetNode>(node_label);
		}else{
				NetNode existNode = mapPanel.getExistNodeFromMap((short) (icmp.hop_limit+1-firstHopPos), node_label);
			if(existNode!=null){
				newNode= new Node<NetNode>(existNode);
			}
		}
		//add new node_label to routeList to check is this route exist
		routeList.add(node_label);
		
		if(newNode!=null){
		latestNode.addChild(newNode);
		latestNode=newNode;
		}
		TraceRouteWindow.mapPanelRefresh(showType);
	}
	
	class MouseDragPicListener implements MouseInputListener{

		NetNode sourceLabel;
		Point p= new Point(0,0);
		
		public MouseDragPicListener(NetNode label){
			sourceLabel=label;
		}
	
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
//			System.out.println("pressed");
			//get current position
			p=SwingUtilities.convertPoint(sourceLabel, e.getPoint(), mapPanel);
			mapPanel.repaint();
			
		
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {
//			System.out.println("Draged");
			Point newPoint= SwingUtilities.convertPoint(sourceLabel,e.getPoint() ,mapPanel);
			sourceLabel.setLocation(sourceLabel.getX()+(newPoint.x-p.x),sourceLabel.getY()+(newPoint.y-p.y));
			sourceLabel.setPosPoint(sourceLabel.getLocation());
			p=newPoint;
			mapPanel.repaint();
		}

		@Override
		public void mouseMoved(MouseEvent arg0) {
			
			
		}
		
		
	}
	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentResized(ComponentEvent e) {
		
		System.out.println("resized");
		System.out.println("mapPAnel"+mapPanel.getSize());
		
		 
		// TODO Auto-generated method stub
//		mapPanel.setLayout(null);
		TraceRouteWindow.mapPanelRefresh(showType);
		
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
