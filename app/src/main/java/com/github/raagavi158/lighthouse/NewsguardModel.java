package com.github.raagavi158.lighthouse;

import org.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


@JsonIgnoreProperties
public class NewsguardModel {
    public String createdDate;
    public String id;
    public String profileId;
    public String identifier;
    public String topline;
    public String rank;
    public int score;
    public String country;
    public String language;
    public JSONObject writeup;
    public JSONObject criteria;
    public boolean active;
    public boolean healthGuard;
    public String locale;

    public NewsguardModel() {

    }

    public NewsguardModel(
            String createdDate,
            String id,
            String profileId,
            String identifier,
            String topline,
            String rank,
            int score,
            String country,
            String language,
            JSONObject writeup,
            JSONObject criteria,
            boolean active,
            boolean healthGuard,
            String locale
    ) {
        this.createdDate = createdDate;
        this.id = id;
        this.profileId = profileId;
        this.identifier = identifier;
        this.topline = topline;
        this.rank = rank;
        this.score = score;
        this.country = country;
        this.language = language;
        this.writeup = writeup;
        this.criteria = criteria;
        this.active = active;
        this.healthGuard = healthGuard;
        this.locale = locale;
    }
}