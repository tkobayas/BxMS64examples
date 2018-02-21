package com.sample;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.task.UserGroupCallback;

public class MyUserGroupCallbackImpl implements UserGroupCallback {

	// This jar doesn't contain beans.xml
	
    public MyUserGroupCallbackImpl() {
        System.out.println("MyUserGroupCallbackImpl: NO-ARG");
    }

    public boolean existsGroup(String groupId) {
        System.out.println("MyUserGroupCallbackImpl: existsGroup()");
        return true;
    }

    public boolean existsUser(String userId) {
        System.out.println("MyUserGroupCallbackImpl: existsUser()");
        return true;
    }

    public List<String> getGroupsForUser(String userId, List<String> groupIds, List<String> allExistingGroupIds) {
        System.out.println("MyUserGroupCallbackImpl: getGroupsForUser()");
        List<String> groupsList = new ArrayList<>();
        groupsList.add("PM");
        groupsList.add("HR");
        return groupsList;
    }
}
