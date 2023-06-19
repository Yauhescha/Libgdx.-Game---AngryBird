package com.hescha.game;

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
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
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
    }

    @Override
    public void render(float delta) {
        update(delta);
        clearScreen();
        draw();
        drawDebug();
    }

    private void update(float delta) {
        world.step(delta, 6, 2);
        box2dCam.position.set(UNIT_WIDTH / 2, UNIT_HEIGHT / 2, 0);
        box2dCam.update();
//        body.setAwake(true);
    }

    private void clearScreen() {
        ScreenUtils.clear(Color.BLACK);
    }

    private void draw() {
        batch.setProjectionMatrix(camera.projection);
        batch.setTransformMatrix(camera.view);
        orthogonalTiledMapRenderer.render();
        debugRenderer.render(world, box2dCam.combined);
//        batch.begin();
//
//        batch.end();
    }

    private void drawDebug() {
        shapeRenderer.setProjectionMatrix(camera.projection);
        shapeRenderer.setTransformMatrix(camera.view);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);


        shapeRenderer.end();
    }

}
