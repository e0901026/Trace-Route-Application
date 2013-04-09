package edu.vamk.netdatacapture.component;

import java.awt.Font;
import java.awt.Point;
import java.net.InetAddress;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class NetNode extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4898988925643607526L;
	
	private List<JLabel> linkList;
	
	private boolean isDestination;
	
	private Point posPoint;
	
	private InetAddress inNetAddr;
	
	private String srcAddress;
	
	private ImageIcon node;
	
	Font f = new Font("Dialog", Font.PLAIN, 8);
	
	public NetNode(){
		super();
	}

	public NetNode(ImageIcon node) {
		super(node);
		this.setNode(node);
		this.setFont(f);
//		setLinkList(new ArrayList<JLabel>());
//		isDestination=false;
		// TODO Auto-generated constructor stub
	}
	
	public List<JLabel> getLinkList() {
		return linkList;
	}

	public void setLinkList(List<JLabel> linkList) {
		this.linkList = linkList;
	}

	public boolean isDestination() {
		return isDestination;
	}

	public void setDestination(boolean isDestination) {
		this.isDestination = isDestination;
	}

	public Point getPosPoint() {
		return posPoint;
	}

	public void setPosPoint(Point posPoint) {
		this.posPoint = posPoint;
	}

	public InetAddress getInNetAddr() {
		return inNetAddr;
	}

	public void setInNetAddr(InetAddress inNetAddr) {
		this.inNetAddr = inNetAddr;
	}

	public String getSrcAddress() {
		return srcAddress;
	}

	public void setSrcAddress(String srcAddress) {
		this.srcAddress = srcAddress;
	}

	public ImageIcon getNode() {
		return node;
	}

	public void setNode(ImageIcon node) {
		this.node = node;
	}

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
