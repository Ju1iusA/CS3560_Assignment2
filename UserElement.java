package MiniTwitter;

import java.util.HashSet;
import java.util.Set;

//Interface that tracks users and groups
interface UserElement {
    void visitUser(User user);
    void visitUserGroup(UserGroup group);
}

//Analyzes and counts system data
class AnalysisVisitor implements UserElement {
    private int userCount = 0;
    private int groupCount = 0;
    private int messageCount = 0;
    private int positiveMessageCount = 0;
    
    //Variables for tracking ID rules and last updated user data
    private final Set<String> uniqueIds = new HashSet<>();
    private boolean containsInvalidIds = false;
    private long latestTimestamp = -1;
    private String lastUpdatedUser = "None";

    //Analyzes user and their tweets
    public void visitUser(User user) {
        userCount++;
        
        //ID Verification Check for spaces and duplicates
        String currentId = user.getId();
        if (currentId.contains(" ") || uniqueIds.contains(currentId)) {
            containsInvalidIds = true;
        }
        uniqueIds.add(currentId);
        
        //Last updated user check
        if (user.getLastUpdateTime() > latestTimestamp) {
            latestTimestamp = user.getLastUpdateTime();
            lastUpdatedUser = currentId;
        }

        for (String tweet : user.getNewsFeed()) {
            messageCount++;
            String textOnly = tweet.substring(tweet.indexOf(":") + 1).toLowerCase();
            if (textOnly.contains("good") || textOnly.contains("great") || textOnly.contains("excellent")) {
                positiveMessageCount++;
            }
        }
    }

    //Counts user groups
    public void visitUserGroup(UserGroup group) {
        if (!group.getId().equals("Root")) {
            groupCount++;
        }
        
        //ID Verification Check
        String currentId = group.getId();
        if (currentId.contains(" ") || uniqueIds.contains(currentId)) {
            containsInvalidIds = true;
        }
        uniqueIds.add(currentId);
    }

    //Getters for data
    public int getUserCount() {
    	return userCount;
    }
    public int getGroupCount() {
    	return groupCount;
    }
    public int getMessageCount() {
    	return messageCount;
    }
    public double getPositivePercentage() { 
        return messageCount == 0 ? 0.0 : ((double) positiveMessageCount / messageCount) * 100.0; 
    }
    
    public boolean areAllIdsValid() {
        return !containsInvalidIds;
    }
    
    public String getLastUpdatedUser() {
        return lastUpdatedUser;
    }
}
