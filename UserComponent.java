package MiniTwitter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

interface UserComponent {
    String getId();
}

class User implements UserComponent {
	//User properties
    private final String id;
    private final List<Consumer<String>> followerFeeds = new ArrayList<>();
    private final List<String> followingsIds = new ArrayList<>();
    private final List<String> newsFeed = new ArrayList<>();
    private final List<Runnable> uiListeners = new ArrayList<>();

    public User(String userId) {
        id = userId;
    }

    public String getId() {
        return id;
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

    public UserGroup(String groupId) {
        id = groupId; 
    }

    public String getId() {
        return id;
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
