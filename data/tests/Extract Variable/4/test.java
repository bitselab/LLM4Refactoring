package org.elasticsearch.search.profile.dfs;

import org.elasticsearch.search.profile.dfs.DfsProfilerIT;
import org.junit.Test;

public class DfsProfilerITTest {
    
    DfsProfilerIT dfsProfilerIT = new DfsProfilerIT();

    @Test
    public void testTestProfileDfs() throws Exception {
        dfsProfilerIT.testProfileDfs();
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme