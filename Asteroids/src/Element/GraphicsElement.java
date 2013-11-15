package Element;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

public class GraphicsElement {
    
    Element container;
    Shape rep;
    protected int graphicsSize;
    
    /**
     * Create a GraphicsElement that takes care of all graphics aspect of the element
     * container: the element that this GraphicsElement is supporting.
     */
    public GraphicsElement(Element container) {
        this.container = container;
    }
    
    /**
     * draw everything on the screen.
     */
    protected void plot(Graphics2D a, AffineTransform transform) {
        a.setTransform(transform);
        a.transform(transform);
        
        a.translate(container.physics.position.getX(), container.physics.position.getY());
        a.rotate(container.physics.movingAngle - Math.PI/2);
        a.drawImage(container.getRep(),-graphicsSize/2,-graphicsSize/2,null);
        
        //Testing purpose. Delete in final release
//        a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
//        a.setPaint(Color.RED);
//        a.fill(new Ellipse2D.Double(-container.getRadius(), -container.getRadius(), 2*container.getRadius(), 2*container.getRadius()));
//        a.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
