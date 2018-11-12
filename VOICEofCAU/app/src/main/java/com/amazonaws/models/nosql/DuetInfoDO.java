package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "voiceofcau-mobilehub-17894429-DuetInfo")

public class DuetInfoDO {
    private String _userId;
    private String _songPart;
    private String _songName;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "SongPart")
    @DynamoDBAttribute(attributeName = "SongPart")
    public String getSongPart() {
        return _songPart;
    }

    public void setSongPart(final String _songPart) {
        this._songPart = _songPart;
    }
    @DynamoDBAttribute(attributeName = "SongName")
    public String getSongName() {
        return _songName;
    }

    public void setSongName(final String _songName) {
        this._songName = _songName;
    }

}
