package com.fitbit.api.services;

import android.app.Activity;
import android.content.Loader;

import com.fitbit.api.exceptions.MissingScopesException;
import com.fitbit.api.exceptions.TokenExpiredException;
import com.fitbit.api.loaders.ResourceLoaderFactory;
import com.fitbit.api.loaders.ResourceLoaderResult;
import com.fitbit.api.models.HistoryDistance;
import com.fitbit.api.models.HistorySteps;
import com.fitbit.authentication.Scope;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by KARAN on 17-05-2018.
 */

public class HistoryDistancesService {

    private final static String ACTIVITIES_URL = "https://api.fitbit.com/1/user/-/activities/distance/date/%s/6m.json";
    private static final ResourceLoaderFactory<HistoryDistance> USER_ACTIVITIES_LOADER_FACTORY = new ResourceLoaderFactory<>(ACTIVITIES_URL, HistoryDistance.class);
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static Loader<ResourceLoaderResult<HistoryDistance>> getHistoryDistanceSummaryLoader(Activity activityContext, Date date) throws MissingScopesException, TokenExpiredException {
        return USER_ACTIVITIES_LOADER_FACTORY.newResourceLoader(activityContext, new Scope[]{Scope.activity}, dateFormat.format(date));
    }
}
