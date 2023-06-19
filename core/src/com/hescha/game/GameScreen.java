package com.hescha.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class GameScreen extends ScreenAdapter {
    private static final float WORLD_WIDTH = 960;
    private static final float WORLD_HEIGHT = 544;
    private static float UNITS_PER_METER = 32F;
    private static float UNIT_WIDTH = WORLD_WIDTH / UNITS_PER_METER;
    private static float UNIT_HEIGHT = WORLD_HEIGHT / UNITS_PER_METER;

    private Viewport viewport;
    private OrthographicCamera camera;
    private OrthographicCamera box2dCam;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Array<Body> toRemove = new Array<>();
//    private Body body;

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        world = new World(new Vector2(0, -10F), true);
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        box2dCam = new OrthographicCamera(UNIT_WIDTH, UNIT_HEIGHT);
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply(true);
        shapeRenderer = new ShapeRenderer();

        tiledMap = GameAngryBird.getAssetManager().get("nuttybirds.tmx");
        orthogonalTiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, batch);
        orthogonalTiledMapRenderer.setView(camera);

        TiledObjectBodyBuilder.buildBuildingBodies(tiledMap, world);
        TiledObjectBodyBuilder.buildFloorBodies(tiledMap, world);
        TiledObjectBodyBuilder.buildBirdBodies(tiledMap, world);
        world.setContactListener(new NuttyContactListener());

        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                createBullet();
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw();
        drawDebug();
    }

    private void update(float delta) {
        clearDeadBodies();
        world.step(delta, 6, 2);
        box2dCam.position.set(UNIT_WIDTH / 2, UNIT_HEIGHT / 2, 0);
        box2dCam.update();
    }

    private void clearScreen() {
        ScreenUtils.clear(Color.BLACK);
    }

    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        orthogonalTiledMapRenderer.render();
        debugRenderer.render(world, box2dCam.combined);
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);


        shapeRenderer.end();
    }

    private void createBullet() {
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.5f);
        circleShape.setPosition(new Vector2(3,6));
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        Body bullet = world.createBody(bd);
        bullet.createFixture(circleShape, 0);
        circleShape.dispose();
        bullet.setLinearVelocity(10,6);
    }

    private void clearDeadBodies() {
        for (Body body : toRemove) {
            world.destroyBody(body);
        }
        toRemove.clear();
    }





    class NuttyContactListener implements ContactListener {

        @Override
        public void beginContact(Contact contact) {
            if (contact.isTouching()) {
                Fixture attacker = contact.getFixtureA();
                Fixture defender = contact.getFixtureB();
                WorldManifold worldManifold = contact.getWorldManifold();
                if ("enemy".equals(defender.getUserData())) {
                    Vector2 vel1 = attacker.getBody().
                            getLinearVelocityFromWorldPoint(worldManifold.getPoints()[0]);
                    Vector2 vel2 = defender.getBody().
                            getLinearVelocityFromWorldPoint(worldManifold.getPoints()[0]);
                    Vector2 impactVelocity = vel1.sub(vel2);
                    if (Math.abs(impactVelocity.x) > 1 || Math.abs(impactVelocity.y) > 1) {
                        toRemove.add(defender.getBody());
                    }
                }
            }
        }

        @Override
        public void endContact(Contact contact) {

        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }
    }
}
