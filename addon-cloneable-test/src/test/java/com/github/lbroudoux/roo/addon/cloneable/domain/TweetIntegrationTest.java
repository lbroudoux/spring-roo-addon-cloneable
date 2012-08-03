package com.github.lbroudoux.roo.addon.cloneable.domain;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.test.RooIntegrationTest;


@RooIntegrationTest(entity = Tweet.class)
public class TweetIntegrationTest {

    @Autowired
    private TweetDataOnDemand dod;
   
    @Test
    public void testCloneableAddOn() {
       Tweet obj1 = dod.getSpecificTweet(1);
       
       // Ensure we've got no clones at start.
       List<Tweet> start = obj1.getClones();
       Assert.assertEquals(0, start.size());
       
       // Clone and check equality.
       Tweet clone1 = obj1.createClone();
       Assert.assertEquals(obj1.getContent(), clone1.getContent());
       Assert.assertEquals(obj1.getAuthor(), clone1.getAuthor());
       Assert.assertEquals(obj1.getOriginal(), clone1.getOriginal());
       Assert.assertEquals(obj1.getRetweets(), clone1.getRetweets());
       Assert.assertNotNull(clone1.getCloneReference());
       Assert.assertEquals(obj1, clone1.getCloneReference());
       
       // Check clone querying.
       List<Tweet> result = obj1.getClones();
       Assert.assertEquals(1, result.size());
    }
}
