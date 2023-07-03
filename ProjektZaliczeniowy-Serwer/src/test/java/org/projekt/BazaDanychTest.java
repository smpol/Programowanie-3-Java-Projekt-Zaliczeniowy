package org.projekt;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class BazaDanychTest {
    public BazaDanych bazaDanych;
    @BeforeEach
    public void setUp() throws SQLException, ClassNotFoundException {
        bazaDanych = new BazaDanych();
    }

    @AfterEach
    public void tearDown() throws SQLException {
        bazaDanych.connection.close();
    }

    @Test
    void checkUserExist() throws SQLException {
        assertFalse(bazaDanych.checkUserExist("nieistniejcy_uzytkownik"));
        assertTrue(bazaDanych.checkUserExist("test"));
    }

    @Test
    void getID() throws SQLException {
        assertEquals(-1, bazaDanych.getID("nieistniejcy_uzytkownik"));
    }
}