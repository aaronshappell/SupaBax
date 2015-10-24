/**
 * 
 */
package crate;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * @author Aaron
 * @author Ryan
 *
 */
public class GameScreen implements Screen, InputProcessor {
	private SupaBax game;
	
	//to switch between debug rendering and normal rendering
	private boolean debug = true;
	
	//box2d necessities
	private Box2DDebugRenderer debugRenderer;
	private World world;
	private BodyBuilder bodyBuilder;
	
	//tmx map stuff
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	
	//camera and viewport
	private OrthographicCamera camera;
	private Viewport viewport;
	
	//Entities in the game
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	private Player player;

	/**
	 * 
	 * @param game
	 */
	public GameScreen(SupaBax game) {
		this.game = game;
		
		//float aspectRatio = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
		
		//create the camera and setup the viewport
		camera = new OrthographicCamera();
		viewport = new FitViewport(24, 16, camera);
		viewport.apply();
		
		//move the camera to the center of the world
		camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
		
		//setup box2d world
		debugRenderer = new Box2DDebugRenderer();
		world = new World(new Vector2(0, -50f), true);
		
		//load the tmx map
		mapLoader = new TmxMapLoader();
		map = mapLoader.load("crate.tmx");
		mapRenderer = new OrthogonalTiledMapRenderer(map, 1f / SupaBax.PPM);
		
		//build the box2d objects
		bodyBuilder = new BodyBuilder();
		bodyBuilder.createBodies(entities, world, map);
		
		player = (Player) entities.get(0);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);
		
		//Set the contact listener for the box2d world to callback to.
		world.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
				contact.resetFriction();
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
			
			@Override
			public void endContact(Contact contact) {
				if(contact.getFixtureA().getUserData() == "ground sensor" || contact.getFixtureB().getUserData() == "ground sensor"){
					player.setGrounded(false);
				}
			}
			
			@Override
			public void beginContact(Contact contact) {
				if(contact.getFixtureA().getUserData() == "ground sensor" || contact.getFixtureB().getUserData() == "ground sensor"){
					player.setGrounded(true);
				}
			}
		});
	}
	
	/**
	 * 
	 * @param delta
	 */
	public void update(float delta){
		for(Entity entity : entities){
			entity.update(delta);
		}
		
		world.step(delta, 6, 2);
		camera.update();
	}

	@Override
	public void render(float delta) {
		update(delta);
		
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(!debug){
			mapRenderer.setView(camera);
			mapRenderer.render();
			
			game.batch.setProjectionMatrix(camera.combined);
			game.batch.begin();
			for(Entity entity : entities){
				entity.render(game.batch, delta);
			}
			game.batch.end();
		} else{
			debugRenderer.render(world, camera.combined);
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		//dispose of all game entities
		for(Entity entity : entities){
			entity.dispose();
		}
		world.dispose();
		debugRenderer.dispose();
		mapRenderer.dispose();
		map.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.ESCAPE){
			Gdx.app.exit();
		}
		if(keycode == Input.Keys.GRAVE){
			if(debug){
				debug = false;
			} else{
				debug = true;
			}
		}
		
		//Handle player movement
		if(keycode == Input.Keys.A){
			player.setMovingLeft(true);
		} else if(keycode == Input.Keys.D){
			player.setMovingRight(true);
		}
		if(keycode == Input.Keys.SPACE){
			player.setJump(true);
		}
		
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.A){
			player.setMovingLeft(false);
		} else if(keycode == Input.Keys.D){
			player.setMovingRight(false);
		}
		if(keycode == Input.Keys.SPACE){
			player.setJump(false);
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
