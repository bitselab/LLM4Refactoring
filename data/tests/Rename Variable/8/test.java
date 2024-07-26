package org.hibernate.search.integrationtest.jakarta.batch.massindexing;

import org.hibernate.search.integrationtest.jakarta.batch.massindexing.MassIndexingJobIT;
import org.junit.Test;

public class MassIndexingJobITTest {
    
    MassIndexingJobIT massIndexingJobIT = new MassIndexingJobIT();

    @Test
    public void testInitData() throws Exception {
        massIndexingJobIT.initData();
    }

    @Test
    public void testSimple() throws Exception {
        massIndexingJobIT.simple();
    }

    @Test
    public void testSimple_defaultCheckpointInterval() throws Exception {
        massIndexingJobIT.simple_defaultCheckpointInterval();
    }

    @Test
    public void testIndexedEmbeddedCollection() throws Exception {
        massIndexingJobIT.indexedEmbeddedCollection();
    }

    @Test
    public void testIndexedEmbeddedCollection_idFetchSize_entityFetchSize_mysql() throws Exception {
        massIndexingJobIT.indexedEmbeddedCollection_idFetchSize_entityFetchSize_mysql();
    }

    @Test
    public void testPurge() throws Exception {
        massIndexingJobIT.purge();
    }

    @Test
    public void testNoPurge() throws Exception {
        massIndexingJobIT.noPurge();
    }

    @Test
    public void testReindexOnly() throws Exception {
        massIndexingJobIT.reindexOnly();
    }

    @Test
    public void testReindexOnly_maxResults() throws Exception {
        massIndexingJobIT.reindexOnly_maxResults();
    }

    @Test
    public void testPartitioned() throws Exception {
        massIndexingJobIT.partitioned();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme