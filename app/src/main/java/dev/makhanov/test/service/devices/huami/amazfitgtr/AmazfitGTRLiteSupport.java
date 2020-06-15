/*  Copyright (C) 2017-2020 Andreas Shimokawa, Carsten Pfeiffer

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
package dev.makhanov.test.service.devices.huami.amazfitgtr;

import android.content.Context;
import android.net.Uri;

import java.io.IOException;

import dev.makhanov.test.devices.huami.HuamiFWHelper;
import dev.makhanov.test.devices.huami.amazfitgtr.AmazfitGTRLiteFWHelper;
import dev.makhanov.test.service.btle.TransactionBuilder;
import dev.makhanov.test.service.devices.huami.amazfitgts.AmazfitGTSSupport;

public class AmazfitGTRLiteSupport extends AmazfitGTSSupport {

    @Override
    public HuamiFWHelper createFWHelper(Uri uri, Context context) throws IOException {
        return new AmazfitGTRLiteFWHelper(uri, context);
    }

    // override to skip requesting GPS version
    @Override
    public void phase2Initialize(TransactionBuilder builder) {
        super.phase2Initialize(builder);
        setLanguage(builder);
    }
}
