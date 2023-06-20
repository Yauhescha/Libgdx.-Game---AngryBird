package com.hescha.game;

import com.badlogic.gdx.utils.Pool;

public class AcornPool extends Pool<Acorn> {
    public static final int ACORN_COUNT = 3;

    public AcornPool() {
        super(ACORN_COUNT, ACORN_COUNT);
    }

    @Override
    protected Acorn newObject() {
        return new Acorn();
    }
}
