package com.sap.bulletinboard.ads.models;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.sql.Timestamp;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sap.bulletinboard.ads.config.EmbeddedDatabaseConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = EmbeddedDatabaseConfig.class)
public class AdvertisementRepositoryTest {

    @Inject
    private AdvertisementRepository repo;
    private Advertisement entity;

    @Before
    public void setUp() {
        entity = new Advertisement();
        entity.setTitle("SOME title");
    }

    @After
    public void tearDown() throws Exception {
        repo.deleteAll();
        assertThat(repo.count(), is(0L));
    }

    @Test
    public void shouldSetIdOnFirstSave() {
        entity = repo.save(entity);
        assertThat(entity.getId(), is(notNullValue()));
    }

    @Test
    public void shouldSetCreatedTimestampOnFirstSaveOnly() throws InterruptedException {
        entity = repo.save(entity);
        Timestamp timestampAfterCreation = entity.getCreatedAt();
        assertThat(timestampAfterCreation, is(notNullValue()));

        entity.setTitle("Updated Title");
        entity.setCreatedAt(Advertisement.now());
        Thread.sleep(2); // Better: mock time!

        entity = repo.save(entity);
        Timestamp timestampAfterUpdate = entity.getCreatedAt();
        assertThat(timestampAfterUpdate, is(timestampAfterCreation));
    }
    
    @Test
    public void shouldSetUpdatedTimestampOnEveryUpdate() throws InterruptedException {
        entity = repo.save(entity);

        entity.setTitle("Updated Title");
        entity = repo.save(entity);
        Timestamp timestampAfterFirstUpdate = entity.getUpdatedAt();
        assertThat(timestampAfterFirstUpdate, is(notNullValue()));

        Thread.sleep(2); // Better: mock time!

        entity.setTitle("Updated Title 2");
        entity = repo.save(entity);
        Timestamp timestampAfterSecondUpdate = entity.getUpdatedAt();
        assertThat(timestampAfterSecondUpdate, is(not(timestampAfterFirstUpdate)));
    }

    @Test(expected = JpaOptimisticLockingFailureException.class)
    public void shouldUseVersionForConflicts() {
        Advertisement entity = new Advertisement();
        entity.setTitle("some title");
        entity = repo.save(entity); // persists entity and sets initial version

        entity.setTitle("entity instance 1");
        Advertisement updatedEntity = repo.save(entity); // returns instance with updated version

        repo.save(entity); // tries to persist entity with outdated version
    }
}
