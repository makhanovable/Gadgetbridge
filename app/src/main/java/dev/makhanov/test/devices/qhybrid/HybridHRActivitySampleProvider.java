package dev.makhanov.test.devices.qhybrid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import dev.makhanov.test.devices.AbstractSampleProvider;
import dev.makhanov.test.entities.DaoSession;
import dev.makhanov.test.entities.HybridHRActivitySample;
import dev.makhanov.test.entities.HybridHRActivitySampleDao;
import dev.makhanov.test.impl.GBDevice;
import dev.makhanov.test.service.devices.qhybrid.parser.ActivityEntry;

public class HybridHRActivitySampleProvider extends AbstractSampleProvider<HybridHRActivitySample> {
    public HybridHRActivitySampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    @Override
    public AbstractDao<HybridHRActivitySample, ?> getSampleDao() {
        return getSession().getHybridHRActivitySampleDao();
    }

    @Nullable
    @Override
    protected Property getRawKindSampleProperty() {
        return null;
    }

    @NonNull
    @Override
    protected Property getTimestampSampleProperty() {
        return HybridHRActivitySampleDao.Properties.Timestamp;
    }

    @NonNull
    @Override
    protected Property getDeviceIdentifierSampleProperty() {
        return HybridHRActivitySampleDao.Properties.DeviceId;
    }

    @Override
    public int normalizeType(int rawType) {
        if(rawType == -1) return 0;
        return ActivityEntry.WEARING_STATE.fromValue((byte) rawType).getActivityKind();
    }

    @Override
    public int toRawActivityKind(int activityKind) {
        return 0;
    }

    @Override
    public float normalizeIntensity(int rawIntensity) {
        return rawIntensity / 63f;
    }

    @Override
    public HybridHRActivitySample createActivitySample() {
        return new HybridHRActivitySample();
    }

    @Override
    public List<HybridHRActivitySample> getActivitySamples(int timestamp_from, int timestamp_to) {
        return super.getActivitySamples(timestamp_from, timestamp_to);
    }

    @Override
    public List<HybridHRActivitySample> getAllActivitySamples(int timestamp_from, int timestamp_to) {
        return super.getAllActivitySamples(timestamp_from, timestamp_to);
    }
}