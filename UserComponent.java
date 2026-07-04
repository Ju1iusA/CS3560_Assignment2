package MiniTwitter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

interface UserComponent {
    String getId();
    long getCreationTime();
}

class User implements UserComponent {
    //User properties
    private final String id;
    private final List<Consumer<String>> followerFeeds = new ArrayList<>();
    private final List<String> followingsIds = new ArrayList<>();
    private final List<String> newsFeed = new ArrayList<>();
    private final List<Runnable> uiListeners = new ArrayList<>();
    
    private final long userCreationTime;
    private long lastUpdateTime;

    public User(String userId) {
        id = userId;
        userCreationTime = System.currentTimeMillis();
        lastUpdateTime = userCreationTime;
    }

    public String getId() {
        return id;
    }
    
    //Getter for creation time
    public long getCreationTime() {
        return userCreationTime;
    }
    
    //Getter to read when the user last tweeted or received a tweet
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String toString() {
        return id;
    }

    public List<String> getFollowingsIds() {
        return followingsIds;
    }

    public List<String> getNewsFeed() {
        return newsFeed;
    }

    //Refreshes UI when something new is added
    public void addUiListener(Runnable listener) {
        uiListeners.add(listener);
    }

    //When receiving from followed users
    public void update(String tweet) {
        lastUpdateTime = System.currentTimeMillis();
        newsFeed.add(tweet);
        uiListeners.forEach(Runnable::run);
    }

    //Allows for following users
    public void follow(User targetUser) {
        if (!followingsIds.contains(targetUser.getId()) && !targetUser.getId().equals(id)) {
            followingsIds.add(targetUser.getId());
            
            Consumer<String> feedUpdater = tweet -> update(tweet);
            targetUser.addFollowerFeed(feedUpdater);
        }
    }

    //Reveals tweet to everyone
    public void postTweet(String message) {
        //Automatically updates the time
        lastUpdateTime = System.currentTimeMillis();
        String formattedTweet = id + ": " + message;
        newsFeed.add(formattedTweet);
        uiListeners.forEach(Runnable::run);
        
        followerFeeds.forEach(feed -> feed.accept(formattedTweet));
    }

    public void addFollowerFeed(Consumer<String> feedUpdater) {
        followerFeeds.add(feedUpdater);
    }
}

//Group container
class UserGroup implements UserComponent {
    private final String id;
    private final List<UserComponent> children = new ArrayList<>();
    
    //Time tracking variable
    private final long groupCreationTime;

    public UserGroup(String groupId) {
        id = groupId; 
        groupCreationTime = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }
    
    public long getCreationTime() {
        return groupCreationTime;
    }

    public String toString() {
        return id;
    }

    //Adds user or group to folder
    public void add(UserComponent component) {
        children.add(component);
    }

    public List<UserComponent> getChildren() {
        return children;
    }
}
