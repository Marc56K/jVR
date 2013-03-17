package de.bht.jvr.core.attributes;

import de.bht.jvr.core.Context;
import de.bht.jvr.core.ContextValueMap;
import de.bht.jvr.logger.Log;
/**
 * This file is part of jVR.
 *
 * Copyright 2013 Marc Roßbach
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class AttributeUpdate {
    private AttributeValues values = null;
    private ContextValueMap<Boolean> needUpdate = new ContextValueMap<Boolean>(true);

    public AttributeUpdate(AttributeValues values) {
        this.values = values;
    }

    public void applyUpdate(Context ctx) throws Exception {
        if (needUpdate.get(ctx)) {
            if (values != null)
                values.update(ctx);

            needUpdate.put(ctx, false);
        }
    }

    public AttributeArray createAttributeArray(Context ctx) throws Exception {
        if (needUpdate.get(ctx)) {
            Log.info(this.getClass(), "Creating vertex attribute array.");

            AttributeArray attribArray = new AttributeArray(values);

            if (!attribArray.isBuilt(ctx))
                attribArray.build(ctx);

            needUpdate.put(ctx, false);

            return attribArray;
        }

        return null;
    }
}
