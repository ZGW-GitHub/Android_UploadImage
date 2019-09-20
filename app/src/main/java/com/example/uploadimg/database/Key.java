package com.example.uploadimg.database;

public class Key {

    private String accesskey;
    private String secretkey;
    private String bucket;
    private String domain;

    public Key(String accesskey, String secretkey, String bucket, String domain) {
        this.accesskey = accesskey;
        this.secretkey = secretkey;
        this.bucket = bucket;
        this.domain = domain;
    }

    public String getAccesskey() {
        return accesskey;
    }

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    public String getSecretkey() {
        return secretkey;
    }

    public void setSecretkey(String secretkey) {
        this.secretkey = secretkey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
