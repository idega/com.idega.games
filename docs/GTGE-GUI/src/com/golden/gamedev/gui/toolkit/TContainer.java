package com.golden.gamedev.gui.toolkit;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

import com.golden.gamedev.util.*;

public abstract class TContainer extends TComponent {
	private static final Comparator DEFAULT_COMPARATOR = new Comparator() {
		public int compare(Object o1, Object o2) {
			return ((TComponent) o2).getLayer() - ((TComponent) o1).getLayer();
		}
	};
	private Comparator 	comparator = DEFAULT_COMPARATOR;

	private TComponent[] childs = new TComponent[0];
	private int 		childCount = 0;

	private Shape   	oldClip; 	// to revert old graphics clip area
									// used only if elastic = true
	private boolean 	elastic; 	// true, rendering out of container bounds is clipped

	private TComponent 	latestInserted = null;

	public TContainer(int x, int y, int w, int h) {
		super(x,y,w,h);

		setFocusable(false);
	}

	public boolean isContainer() { return true; }

	/** Appends the specified component to the end of this container. */
	public void add(TComponent comp) {
		if (comp.getContainer() != null) {
			throw new IllegalStateException
				(comp + " already reside in another container!!!");
		}

		comp.setContainer(this);

		childs = (TComponent[]) Utility.expand(childs, 1, false);
		childs[0] = comp;
		childCount++;

		frame.setFrameWork(comp); // set child component frame work

		sortComponents();
		latestInserted = comp;
	}
	/**
	 * Adds the specified component to this container at the given position.
	 */
	public void add(TComponent comp, int index) {
		if (comp.getContainer() != null) {
			throw new IllegalStateException
				(comp + " already reside in another container!!!");
		}

		comp.setContainer(this);

		TComponent[] newChilds = new TComponent[childs.length + 1];
		childCount++;
		int ctr = 0;
		for (int i=0;i < childCount;i++) {
			if (i != index) {
				newChilds[i] = childs[ctr];
				ctr++;
			}
		}
		childs = newChilds;
		childs[index] = comp;

		frame.setFrameWork(comp); // set child component frame work

		sortComponents();
		latestInserted = comp;
	}

	/** Removes the specified component from this container. */
	public int remove(TComponent comp) {
		for (int i=0;i < childCount;i++) {
			if (childs[i] == comp) {
				remove(i);
				return i;
			}
		}

		return -1;
	}
	/** Removes the specified indexth component from this container. */
	public TComponent remove(int index) {
		TComponent comp = childs[index];

		frame.setComponentStat(comp, false);
		comp.setContainer(null);

		childs = (TComponent[]) Utility.cut(childs, index);
		childCount--;

		return comp;
	}
	/** Clears the container (removes all container child components). */
	public void clear() {
		frame.clearComponentsStat(childs);
		for (int i=0;i < childCount;i++) {
			childs[i].setContainer(null);
		}

		childCount = 0;
		childs = new TComponent[0];
	}
	public void replace(TComponent oldComp, TComponent newComp) {
		int index = remove(oldComp);
		add(newComp, index);
	}

	public void update() {
		if (!isVisible()) return;

		super.update();

		for (int i=0;i < childCount;i++) {
			childs[i].update();
		}
	}

	protected void validatePosition() {
		super.validatePosition();

		for (int i=0;i < childCount;i++) {
			childs[i].validatePosition();
		}

		if (!elastic) {
			for (int i=0;i < childCount;i++) {
				if (childs[i].getX() > getWidth() ||
					childs[i].getY() > getHeight() ||
					childs[i].getX() + childs[i].getWidth() < 0 ||
					childs[i].getY() + childs[i].getHeight() < 0) {
					elastic = true;
					break;
				}
			}
		}
	}
	protected void validateSize() {
		super.validateSize();

		for (int i=0;i < childCount;i++) {
			childs[i].validateSize();
		}
	}

	public void render(Graphics2D g) {
		if (!isVisible()) return;

		super.render(g);

		if (elastic) {
			// elastic container means:
			// child component could be outside container bounds
			// so we must set graphics clip
			// to clip components that rendered out of container bounds
			oldClip = g.getClip(); // save old clip
		    g.clipRect(getScreenX(), getScreenY(), getWidth(), getHeight());
		}

		renderComponents(g);

		if (elastic) {
			// revert back graphics clip
			g.setClip(oldClip);
		}
	}
	protected void renderComponents(Graphics2D g) {
		for (int i=childCount-1;i >= 0;i--) {
			childs[i].render(g);
		}
	}

	public void sendToFront(TComponent comp) {
		if (childCount <= 1 || childs[0] == comp) {
			return;
		}

		for (int i=0;i < childCount;i++) {
			if (childs[i] == comp) {
				// algorithm:
				// - remove from old position
				// - send to front
				// - then sort
				childs = (TComponent[]) Utility.cut(childs, i);
				childs = (TComponent[]) Utility.expand(childs, 1, false);
				childs[0] = comp;
				sortComponents();
				break;
			}
		}
	}
	public void sendToBack(TComponent comp) {
		if (childCount <= 1 || childs[childCount-1] == comp) {
			return;
		}

		for (int i=0;i < childCount;i++) {
			if (childs[i] == comp) {
				// algorithm:
				// - remove from old position
				// - send to back
				// - then sort
				childs = (TComponent[]) Utility.cut(childs, i);
				childs = (TComponent[]) Utility.expand(childs, 1, true);
				childs[childCount-1] = comp;
				sortComponents();
				break;
			}
		}
	}
	public void sortComponents() {
		Arrays.sort(childs, comparator);
	}

	/** Transfers the specified child component focus forward. */
	protected void transferFocus(TComponent component) {
		for (int i=0;i < childCount;i++) {
			if (component == childs[i]) {
				int j = i;
				do {
					if (--i < 0) i = childCount-1;
					if (i == j) return;
				} while (!childs[i].requestFocus());

				break;
			}
		}
	}
	/** Transfers the specified child component focus backward. */
	protected void transferFocusBackward(TComponent component) {
		for (int i=0;i < childCount;i++) {
			if (component == childs[i]) {
				int j = i;
				do {
					if (++i >= childCount) i = 0;
					if (i == j) return;
				} while (!childs[i].requestFocus());

				break;
			}
		}
	}

	/**
	 * Returns true whether this container is being selected.
	 * If any of this container childs is being selected,
	 * this container is marked as selected too.
	 */
	public boolean isSelected() {
		if (!super.isSelected()) {
			for (int i=0;i < childCount;i++) {
				if (childs[i].isSelected()) {
					// child component is being selected
					// mark this container as selected too
					return true;
				}
			}
			return false;

		} else return true;
	}

	public boolean isElastic() { return elastic; }
	public void setElastic(boolean b) { elastic = b; }

	public Comparator getComparator() { return comparator; }
	public void setComparator(Comparator c) {
		if (c == null) throw new NullPointerException("Comparator can not null");

		comparator = c;
		sortComponents();
	}

	/**
	 * Finds visible child component in specified screen coordinate.
	 * The top-most child component is returned in the case of
	 * overlap child components. <p>
	 * If the founded child component is also a container,
	 * this method will continue searching for the deepest nested
	 * child component.
	 */
	public TComponent findComponent(int x1, int y1) {
		if (!intersects(x1, y1)) return null;

		for (int i=0;i < childCount;i++) {
			if (childs[i].intersects(x1, y1)) {
				// recursive find component
				TComponent comp = (childs[i].isContainer() == false) ?
					childs[i] : ((TContainer) childs[i]).findComponent(x1, y1);
				return comp;
			}
		}

		return this;
	}

	/** Returns the number of components in this container. */
	public int getComponentCount() { return childCount; }
	/** Returns all child components in this container. */
	public TComponent[] getComponents() { return childs; }

	/** Returns the latest inserted component into this container. */
	public TComponent get() { return latestInserted; }

}