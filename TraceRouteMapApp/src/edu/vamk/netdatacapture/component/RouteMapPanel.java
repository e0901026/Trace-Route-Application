package edu.vamk.netdatacapture.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.vamk.netdatacapture.ui.TraceRouteWindow;

public class RouteMapPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2642129441985080494L;
	
//	private Map<MapNodeMarker, NetNode> node_map= new HashMap<MapNodeMarker, NetNode>();
	
	
	private Tree<NetNode> routeMapTree;
	
	private Map<Short, List<NetNode>> nodeMap;
	
	
	
	
	public RouteMapPanel(){
		super();
		setLayout(null);
		setNodeMap(new HashMap<Short, List<NetNode>>());
		setRouteMapTree(new Tree<NetNode>());
//		ImageIcon node = new ImageIcon("img/no.png");
		ImageIcon node = new ImageIcon(getClass().getResource("/img/no.png"));
		
		NetNode netnode=new NetNode(node);
		Point nodePosPoint= new Point(0,10);
		netnode.setName("local pc");
		netnode.setSrcAddress("127.0.0.1");
		String labelText = String.format(TraceRouteWindow.LABELSTYLE, TraceRouteWindow.LABELSIZE,netnode.getName());
		netnode.setText(labelText);
		netnode.setHorizontalTextPosition(JLabel.CENTER);
		netnode.setVerticalTextPosition(JLabel.BOTTOM);
		netnode.setPosPoint(nodePosPoint);
//		System.out.println(netnode.getPreferredSize());
		netnode.setBounds(netnode.getPosPoint().x+getInsets().left, netnode.getPosPoint().y + getInsets().top, netnode.getPreferredSize().width, netnode.getPreferredSize().height);
		this.add(netnode);
		Node<NetNode> rootNode = new Node<NetNode>(netnode);
		rootNode.setNodeLevel(new Short("0"));
		addNodeToMap(new Short("0"), netnode);
		getRouteMapTree().setRootElement(rootNode); 
	}


	public Tree<NetNode> getRouteMapTree() {
		return routeMapTree;
	}

	public void setRouteMapTree(Tree<NetNode> routeMapTree) {
		this.routeMapTree = routeMapTree;
	}


	


	public Map<Short, List<NetNode>> getNodeMap() {
		return nodeMap;
	}


	public void setNodeMap(Map<Short, List<NetNode>> nodeMap) {
		this.nodeMap = nodeMap;
	}
	
	public boolean addNodeToMap(Short hopNum, NetNode node_label){
		boolean retVal= true;
		
		if(nodeMap.get(hopNum) == null){
			List<NetNode> nodeList = new ArrayList<NetNode>();
			nodeList.add(node_label);
			nodeMap.put(hopNum, nodeList);
		}else{
			//if node exist in the map
			List<NetNode> nodelist = nodeMap.get(hopNum);
			//avoid node overlapping
			Point nodePosition= new Point(0,0);
			for(NetNode n:nodelist){
				if(n.getPosPoint().y>=nodePosition.y){
					nodePosition.x=n.getPosPoint().x;
					nodePosition.y=n.getPosPoint().y+n.getPreferredSize().height+10;
				}
				if(node_label.getSrcAddress().equals(n.getSrcAddress()))
					return false;
			}
			
			node_label.setPosPoint(nodePosition);
			nodelist.add(node_label);
		}
		
		return retVal;
		
		
		
	}

	public NetNode getExistNodeFromMap(short hopNum, NetNode node_label) {
		
		List<NetNode> nodelist = nodeMap.get(hopNum);
		for(NetNode n:nodelist){
			if(node_label.getSrcAddress().equals(n.getSrcAddress()))
				return n;
		}
		return null;
	}


	


	private void drawRouteMapTree(Node<NetNode> rootElement, Graphics2D g2) {
		if(rootElement.getChildren().size()!=0){
			for(Node<NetNode> node:rootElement.getChildren()){
				NetNode nodeParent=rootElement.getData(); 
				NetNode nodeChild = node.getData();
//				System.out.println(nodeParent.getPosPoint());
//				System.out.println(nodeChild.getPosPoint());
				
				
				Point startPoint= new Point(nodeParent.getPosPoint().x+(nodeParent.getWidth()/2)+(nodeParent.getNode().getIconWidth()/2), nodeParent.getPosPoint().y+nodeParent.getHeight()/2);
				Point endPoint= new Point(nodeChild.getPosPoint().x+(nodeParent.getWidth()/2)-(nodeParent.getNode().getIconWidth()/2), nodeChild.getPosPoint().y+nodeChild.getHeight()/2);
				Line2D line= new Line2D.Float(startPoint, endPoint);
				g2.draw(line);
				drawRouteMapTree(node,g2);
				
			}
		}
		
	}



	public void refreshPanelSize(){
	this.setPreferredSize(new Dimension(5000,1000));
	}
	
//	public void nodeMapChanged(Map<MapNodeMarker, NetNode> _node_map){
//		node_map= _node_map;
//		repaint();
//	}
	
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		Graphics2D g2= (Graphics2D) g;
		g2.setPaint(Color.black);
//		System.out.println(routeMapTree.getRootElement().getChildren().size());
		drawRouteMapTree(routeMapTree.getRootElement(), g2);
		
//		for (Map.Entry<, List<NetNode>> entry : nodeMap.entrySet()) {
//			MapNodeMarker nodeMarker=entry.getKey();
//			NetNode nodeLabel= entry.getValue();
//		
//			nodeLabel.setLocation(nodeLabel.getPosPoint());
////			if node map not euqals to destination 
//			if(!nodeLabel.isDestination()){
////				
////			NetNode nodeNextLabel= node_map.get(Short.parseShort((nodeMarker+1)+""));	
////			System.out.println(nodeMarker);
////			System.out.println(nodeNextLabel);
////			if(nodeNextLabel!=null){
////					System.out.println("draw");
////				Line2D line= new Line2D.Float(nodeLabel.getPosPoint(), nodeNextLabel.getPosPoint());
////				
////				g2.draw(line);
//				
//				
//					
//			}
//			}
			
		
		}	
	
}
