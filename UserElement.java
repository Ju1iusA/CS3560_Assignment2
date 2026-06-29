package MiniTwitter;

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

    //Analyzes user and their tweets
    public void visitUser(User user) {
        userCount++;
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
}
