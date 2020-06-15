/*  Copyright (C) 2017-2020 Carsten Pfeiffer, Daniele Gobbetti

    This file is part of Gadgetbridge.

    Gadgetbridge is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Gadgetbridge is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */
package dev.makhanov.test.adapter;

import android.content.Context;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import dev.makhanov.test.GBApplication;
import dev.makhanov.test.database.DBHandler;
import dev.makhanov.test.database.DBHelper;
import dev.makhanov.test.entities.BaseActivitySummary;
import dev.makhanov.test.entities.BaseActivitySummaryDao;
import dev.makhanov.test.entities.Device;
import dev.makhanov.test.impl.GBDevice;
import dev.makhanov.test.model.ActivityKind;
import dev.makhanov.test.util.DateTimeUtils;
import dev.makhanov.test.util.GB;

public class ActivitySummariesAdapter extends AbstractItemAdapter<BaseActivitySummary> {
    private final GBDevice device;

    public ActivitySummariesAdapter(Context context, GBDevice device) {
        super(context);
        this.device = device;
        loadItems();
    }

    @Override
    public void loadItems() {
        try (DBHandler handler = GBApplication.acquireDB()) {
            BaseActivitySummaryDao summaryDao = handler.getDaoSession().getBaseActivitySummaryDao();
            Device dbDevice = DBHelper.findDevice(device, handler.getDaoSession());

            QueryBuilder<BaseActivitySummary> qb = summaryDao.queryBuilder();
            qb.where(BaseActivitySummaryDao.Properties.DeviceId.eq(dbDevice.getId())).orderDesc(BaseActivitySummaryDao.Properties.StartTime);
            List<BaseActivitySummary> allSummaries = qb.build().list();
            setItems(allSummaries, true);
        } catch (Exception e) {
            GB.toast("Error loading activity summaries.", Toast.LENGTH_SHORT, GB.ERROR, e);
        }
    }

    @Override
    protected String getName(BaseActivitySummary item) {
        String name = item.getName();
        if (name != null && name.length() > 0) {
            return name;
        }

        Date startTime = item.getStartTime();
        if (startTime != null) {
            return DateTimeUtils.formatDateTime(startTime);
        }
        return "Unknown activity";
    }

    @Override
    protected String getDetails(BaseActivitySummary item) {
        return ActivityKind.asString(item.getActivityKind(), getContext());
    }

    @Override
    protected int getIcon(BaseActivitySummary item) {
        return ActivityKind.getIconId(item.getActivityKind());
    }
}
