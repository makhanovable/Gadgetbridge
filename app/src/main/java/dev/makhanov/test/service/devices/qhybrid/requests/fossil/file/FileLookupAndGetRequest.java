/*  Copyright (C) 2019-2020 Daniel Dakhno

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
package dev.makhanov.test.service.devices.qhybrid.requests.fossil.file;

import dev.makhanov.test.service.devices.qhybrid.adapter.fossil.FossilWatchAdapter;

public abstract class FileLookupAndGetRequest extends FileLookupRequest {
    public FileLookupAndGetRequest(byte fileType, FossilWatchAdapter adapter) {
        super(fileType, adapter);
    }

    @Override
    public void handleFileLookup(short fileHandle){
        getAdapter().queueWrite(new FileGetRequest(getHandle(), getAdapter()) {
            @Override
            public void handleFileData(byte[] fileData) {
                FileLookupAndGetRequest.this.handleFileData(fileData);
            }
        }, true);
    }

    abstract public void handleFileData(byte[] fileData);
}
