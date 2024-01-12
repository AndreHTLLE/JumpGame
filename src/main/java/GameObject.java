import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class GameObject {
    private int x;

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    private int y;
    private int width, height;
    private int hitboxWidth, hitboxHeight;
    private boolean passable;
    private Image image;
    private String imagePath;
    private boolean showHitbox;
    private boolean playable;
    private int speed;
    private boolean moveUp = false;
    private boolean moveDown = false;
    private boolean moveLeft = false;
    private boolean moveRight = false;
    private boolean clickable;
    private boolean clicked;
    private boolean hasGravity;
    private HitDirection hitDirection;
    private String objectName;
    private String colldingObjectName;
    private GameObject collidingObject;
    private boolean allHitBoxes;

    private int gravity = 8; // Schwerkraftkonstante
    private boolean hasJumped = false;
    private float jumpTimer = 0.0f; // Timer für die Dauer des Sprungs
    private float maxJumpTime = 0.5f; // Maximale Dauer des Sprungs in Sekunden
    private float passableTimer = 0.0f;


    private GameState gamestate;
    private ArrayList<GameObject> nearbyObjects  = new ArrayList<>();
    private static ArrayList<GameObject> allObjects = new ArrayList<>();


    public void setObjectName(String name) {
        this.objectName = name;
    }
    public String getObjectName(){
        return this.objectName;
    }
    public String getColldingObjectName(){
        if(this.colldingObjectName != null) {
            return this.colldingObjectName;
        } else {
            return "NoName";
        }
    }
    public GameObject(int x, int y, int width, int height, String imagePath, boolean passable) {
        this(x, y, width, height, width, height, imagePath, passable, false);
    }

    public GameObject(int x, int y, int width, int height, String imagePath, boolean passable, boolean showHitbox) {
        this(x, y, width, height, width, height, imagePath, passable, showHitbox);
    }

    public GameObject(int x, int y, int width, int height, int hitboxWidth, int hitboxHeight, String imagePath, boolean passable, boolean showHitbox) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
        this.passable = passable;
        this.image = new ImageIcon(imagePath).getImage();
        this.imagePath = imagePath;
        this.showHitbox = showHitbox;
        this.playable = false;
        this.speed = 5;
        this.clickable = false;
        this.gamestate = GameState.NONE;
        allObjects.add(this);
    }

    // Methode zum Aktualisieren des Timers
    public void updatePassableTimer(float deltaTime) {
        if (passableTimer > 0) {
            passableTimer -= deltaTime;
            if (passableTimer <= 0) {
                setPassable(false); // Macht das Objekt wieder undurchlässig
            }
        }
    }

    // Setter für den Timer
    public void setPassableTimer(float time) {
        this.passableTimer = time;
    }
    public void draw(Graphics g, ImageObserver obs) {
        g.drawImage(image, x, y, width, height, obs);

        if (showHitbox) {
            g.setColor(Color.RED);
            g.drawRect(x, y, hitboxWidth, hitboxHeight);
        }
    }



    public HitDirection move(int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;
        HitDirection hitDirection = HitDirection.NONE;

        int searchRadius = 2;
        Rectangle searchBounds = new Rectangle(newX - searchRadius, newY - searchRadius, hitboxWidth + 2 * searchRadius, hitboxHeight + 2 * searchRadius);
        // Finde alle nahegelegenen Objekte
        nearbyObjects.clear();
        for (GameObject obj : allObjects) {
            if (obj == this) continue;
            Rectangle objBounds = new Rectangle(obj.x, obj.y, obj.hitboxWidth, obj.hitboxHeight);
            if (searchBounds.intersects(objBounds)) {
                nearbyObjects.add(obj);
            }
        }
        // Prüfe Kollisionen separat für X- und Y-Richtung
        HitDirection xDirection = checkCollision(newX, y, dx, 0);
        HitDirection yDirection = checkCollision(x, newY, 0, dy);

        // Aktualisiere die Position basierend auf den Kollisionen
        if (xDirection == HitDirection.NONE) {
            x = newX;
        }
        if (yDirection == HitDirection.NONE) {
            y = newY;
        }

        // Kombiniere die Kollisionsrichtungen, wenn nötig
        if (xDirection != HitDirection.NONE) {
            hitDirection = xDirection;
        } else if (yDirection != HitDirection.NONE) {
            hitDirection = yDirection;
        }
        this.hitDirection = hitDirection;
        return hitDirection;
    }

    public GameObject getCollidingObject() {
        return collidingObject;
    }

    public void setCollidingObject(GameObject collidingObject) {
        this.collidingObject = collidingObject;
    }

    private HitDirection checkCollision(int newX, int newY, int dx, int dy) {
        Rectangle newBounds = new Rectangle(newX, newY, hitboxWidth, hitboxHeight);

        for (GameObject obj : nearbyObjects) {
            Rectangle objBounds = new Rectangle(obj.x, obj.y, obj.hitboxWidth, obj.hitboxHeight);

            if (newBounds.intersects(objBounds)) {
                if (!obj.passable) {
                    adjustPosition(objBounds, dx, dy);
                    this.colldingObjectName = obj.getObjectName();
                    System.out.print(this.getObjectName() + " Colliding: " + colldingObjectName +" ");
                    this.collidingObject = obj;
                    return calculateHitDirection(newBounds, objBounds);

                }
            }
        }
        return HitDirection.NONE;
    }
    private HitDirection calculateHitDirection(Rectangle newBounds, Rectangle objBounds) {
        // Distanzen zwischen den Kanten der beiden Rechtecke berechnen
        int distanceToLeft = Math.abs(newBounds.x - (objBounds.x + objBounds.width));
        int distanceToRight = Math.abs((newBounds.x + newBounds.width) - objBounds.x);
        int distanceToTop = Math.abs(newBounds.y - (objBounds.y + objBounds.height));
        int distanceToBottom = Math.abs((newBounds.y + newBounds.height) - objBounds.y);

        // Finde die minimale Distanz
        int minDistance = Math.min(Math.min(distanceToLeft, distanceToRight), Math.min(distanceToTop, distanceToBottom));

        // Bestimme die Aufprallrichtung basierend auf der minimalen Distanz
        if (minDistance == distanceToLeft) {
            return HitDirection.RIGHT;
        } else if (minDistance == distanceToRight) {
            return HitDirection.LEFT;
        } else if (minDistance == distanceToTop) {
            return HitDirection.DOWN;
        } else if (minDistance == distanceToBottom) {
            this.hasJumped = false;
            return HitDirection.UP;
        } else {
            return HitDirection.NONE;
        }
    }

    private void adjustPosition(Rectangle objBounds, int dx, int dy) {
        if (dx > 0) { // Moving right
            x = objBounds.x - hitboxWidth;
        } else if (dx < 0) { // Moving left
            x = objBounds.x + objBounds.width;
        }

        if (dy > 0) { // Moving down
            y = objBounds.y - hitboxHeight;
        } else if (dy < 0) { // Moving up
            y = objBounds.y + objBounds.height;
        }
    }

    public void updatePosition() {
        int dx = 0, dy = 0;
        if (playable) {
            if (moveLeft) {
                dx -= speed;
            }
            if (moveRight) {
                dx += speed;
            }

            if (!hasGravity || (hasGravity && !hasJumped)) {
                if (moveUp && !hasGravity) {
                   dy -= speed;
                }
                if (moveDown) {
                    dy += speed;
                }
            }
            if (hasGravity) {
                // Starte den Sprung, wenn die Bedingung erfüllt ist
                if (moveUp && this.hitDirection == HitDirection.UP && !hasJumped) {
                    this.hasJumped = true;
                    jumpTimer = maxJumpTime;
                }

                // Aktualisiere die Sprungdynamik
                if (hasJumped) {
                    if (jumpTimer > 0) {
                        // Während des Sprungs
                        dy -= (int)(speed * jumpTimer * 5); // Sprunghöhe basierend auf dem Timer
                        jumpTimer -= 0.05; // Reduziere den Timer
                    } else {
                        // Wenn der Sprung beendet ist
                        hasJumped = false;
                    }
                }
                dy += gravity; // Füge Schwerkraft hinzu
            }
            // Bewege das Objekt
            System.out.println(move(dx, dy));
        } else if(hasGravity){
            dy += speed;
            move(dx,dy);
        }
    }



    public void setMoveDirection(int key, boolean isPressed) {
        if (playable) {
            switch (key) {
                case KeyEvent.VK_W -> moveUp = isPressed;
                case KeyEvent.VK_S -> moveDown = isPressed;
                case KeyEvent.VK_A -> moveLeft = isPressed;
                case KeyEvent.VK_D -> moveRight = isPressed;
            }
        }
    }

    // Methode, um zu prüfen, ob die Maus innerhalb des Objekts geklickt hat
    public boolean checkIfClicked(MouseEvent e) {
        if (clickable) {
            Rectangle bounds = new Rectangle(x, y, hitboxWidth, hitboxHeight);
            if (bounds.contains(e.getPoint())) {
                clicked = true;
                return true;
            } else {
                clicked = false;
            }
        }
        return false;
    }
    public void forceJump(float jumpDuration) {
        if (hasGravity) {
            this.hasJumped = true;
            this.jumpTimer = jumpDuration;
        }
    }

    public void setJumpTime(float i) {
        this.maxJumpTime  = i;
    }
    public void setColldingObjectName(String name) {
        this.colldingObjectName = name;
    }
    public float getJumpTime() {
        return this.maxJumpTime;
    }
    public int getXDistance(GameObject other) {
        return this.x - other.x;
    }
    public int getYDistance(GameObject other) {
            return this.y - other.y;
    }

    public static ArrayList<GameObject> getAllObjects() {
        return allObjects;
    }
    public void setHitboxWidth(int newWidth) {
        this.hitboxWidth = newWidth;
    }

    public void setHitboxHeight(int newHeight) {
        this.hitboxHeight = newHeight;
    }

    public void setHitboxDimensions(int newWidth, int newHeight) {
        this.hitboxWidth = newWidth;
        this.hitboxHeight = newHeight;
    }
    public void setSpeed(int speed) {
        // Setter für die Geschwindigkeit
        this.speed = speed;
    }

    public void setPlayable(boolean playable) {
        // Setter für die Steuerbarkeit
        this.playable = playable;
    }
    public void setGravity(boolean gravity) {
        this.hasGravity = gravity;
    }
    public boolean hasGravity() {
        return this.hasGravity;
    }
    public boolean isClicked() {
        return clicked;
    }

    public void setPassable(Boolean passable) {
        this.passable = passable;
    }
    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }
    public int getGravity() {
        return this.gravity;
    }

    public boolean isClickable() {
        return clickable;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }
    public HitDirection getHitDirection() {
        return this.hitDirection;
    }

    @Override
    public String toString() {
        String fileName = imagePath.substring(imagePath.lastIndexOf("/") + 1);

        return "GameObject{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", passable=" + passable +
                ", ImageData=" + image +
                ", ImageName=" + fileName +
                '}';
    }

    public void remove() {
        this.setGamestate(GameState.NONE);
        this.move(2000,2000);
    }
    public void setGamestate(GameState gamestate) {
        this.gamestate = gamestate;
    }

    public GameState getGamestate() {
        return gamestate;
    }
    public boolean isPlayable() {
        return playable;
    }

    public boolean isPassable() {
        return this.passable;
    }

    public float getPassableTimer() {
        return this.passableTimer;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public void setShowHitbox(Boolean showHitbox) {
        this.showHitbox = showHitbox;
    }
    public void showAllHitBoxes(Boolean onOff) {
        this.allHitBoxes = onOff;
        if(allHitBoxes) {
            for(GameObject gameObject : GameObject.getAllObjects()) {
                gameObject.setShowHitbox(true);
            }
        } else {
            for(GameObject gameObject : GameObject.getAllObjects()) {
                gameObject.setShowHitbox(false);
            }
        }
    }
}
                    //Old
/* public HitDirection move(int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;
        HitDirection hitDirection = HitDirection.NONE;

        // Berechne den Bereich, in dem nach nahegelegenen Objekten gesucht werden soll
        int searchRadius = 2;
        Rectangle searchBounds = new Rectangle(newX - searchRadius, newY - searchRadius, hitboxWidth + 2 * searchRadius, hitboxHeight + 2 * searchRadius);

        // Finde alle nahegelegenen Objekte
        nearbyObjects.clear();
        for (GameObject obj : allObjects) {
            if (obj == this) continue;
            Rectangle objBounds = new Rectangle(obj.x, obj.y, obj.hitboxWidth, obj.hitboxHeight);
            if (searchBounds.intersects(objBounds)) {
                nearbyObjects.add(obj);
            }
        }

        for (GameObject obj : nearbyObjects) {
            Rectangle newBounds = new Rectangle(newX, newY, hitboxWidth, hitboxHeight);
            Rectangle objBounds = new Rectangle(obj.x, obj.y, obj.hitboxWidth, obj.hitboxHeight);

            if (newBounds.intersects(objBounds)) {
                if (!obj.passable) {
                    hitDirection = calculateHitDirection(newBounds, objBounds);
                    // Position basierend auf der Richtung der Kollision anpassen
                    adjustPosition(objBounds, dx, dy);
                    return hitDirection;
                }
            }
        }

        // Position aktualisieren, wenn keine Kollision aufgetreten ist
        x = newX;
        y = newY;
        return hitDirection;
    }*/