/**
 * 
 */
package weapon;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import crate.GameScreen;

/**
 * Definition of a pistol bullet.
 * @author Aaron
 * @author Ryan
 *
 */
public class PistolBullet extends Bullet {
	private World world;

	/**
	 * 
	 * @param gameScreen
	 * @param playerPosition
	 * @param direction
	 */
	public PistolBullet(GameScreen gameScreen, Vector2 playerPosition, float direction) {
		super(gameScreen, 0.5f, 0.5f, direction, 1f, 5f);
		this.world = gameScreen.getWorld();
		
		//Setup the animations
		sheet = new Texture(Gdx.files.internal("spritesheets/weapons/basicbullet.png"));
		TextureRegion[][] splitSheet = TextureRegion.split(sheet, 32, 32);
		
		TextureRegion[] animationFrames = new TextureRegion[4];
		for(int i = 0; i < 4; i++){
			animationFrames[i] = splitSheet[0][i];
		}
		animation = new Animation(0.1f, animationFrames);
		
		stateTime = 0;

		//Scale the sprite to meters
		sprite = new Sprite();
		sprite.setSize(width, height);
		sprite.setOriginCenter();
		
		//Body definition
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(new Vector2(playerPosition.x + (0.8f * direction), playerPosition.y));
		bodyDef.bullet = true;

		body = world.createBody(bodyDef);
		body.setUserData(this);
		
		//bullet body shape definition
		PolygonShape physicsShape = new PolygonShape();
		physicsShape.setAsBox(width / 2f, height / 2f);
		physicsFixture = body.createFixture(physicsShape, 1f);
		physicsFixture.setUserData("bpf");
		physicsFixture.setFriction(0f);
		
		physicsShape.dispose();
		
		body.setGravityScale(0);
		
		Random random = new Random();
		Vector2 impulse = new Vector2(speed, 0f);
		float spread = 2f;
		if(direction < 0){
			impulse.setAngle((180f - spread / 2f) + random.nextFloat() * spread);
		} else if(direction > 0){
			impulse.setAngle(-spread / 2f + (random.nextFloat() * spread));
		}
		body.applyLinearImpulse(impulse, body.getPosition(), true);
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(SpriteBatch batch, float delta) {
		stateTime += delta;
		sprite.setRegion(animation.getKeyFrame(stateTime, true));
		sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
		sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
		if(direction == -1f){
			sprite.setFlip(true, false);
		}
		sprite.draw(batch);
	}

	@Override
	public void dispose() {
		destroyBodies();
		sheet.dispose();
		System.out.println("Pistol bullet disposed");
	}

	@Override
	public void destroyBodies() {
		world.destroyBody(body);
	}

}
