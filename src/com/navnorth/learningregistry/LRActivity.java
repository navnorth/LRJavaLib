/**
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navnorth.learningregistry;

import com.navnorth.learningregistry.util.StringUtil;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Map;
import java.util.HashMap;

import java.util.List;
import java.util.ArrayList;


/**
 * Utility class for building activity paradata for the Learning Registry Exporter
 * The map built by the getData() function is appropriate to use as resource data in the exporter
 *
 * @version 0.1.1
 * @since 2011-11-17
 * @author Todd Brown / Navigation North
 *      <br>
 *      Copyright © 2011 Navigation North Learning Solutions LLC
 *      <br>
 *      Licensed under the Apache License, Version 2.0 (the "License"); See LICENSE
 *      and README.md files distributed with this work for additional information
 *      regarding copyright ownership.
 */
public class LRActivity extends LREnvelope
{
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    private static final String[] payloadSchemaValue = {"LR Paradata 1.0"};
    private static final String payloadPlacementValue = "inline";
    private static final String resourceDataTypeValue = "paradata";

    List<Object> related = new ArrayList<Object>();

    /**
     * Create a new activity with specified details
     *
     * @param resourceLocator value for "resource_locator"
     * @param submitter value for "submitter"
     * @param submitterType value for "submitter_type"
     * @param submissionTOS value for "submission_TOS"
     * @param submissionAttribution value for "submission_attribution"
     * @param signer value for "signer"
     */
    public LRActivity(String resourceLocator, String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer)
    {
        this.resourceData = new HashMap<String, Object>();
        this.resourceDataType = resourceDataTypeValue;
        this.resourceLocator = StringUtil.nullifyBadInput(resourceLocator);
        this.curator = null;
        this.owner = null;
        this.tags = null;
        this.payloadPlacement = payloadPlacementValue;
        this.payloadSchemaLocator = null;
        this.payloadSchema = payloadSchemaValue;
        this.submissionTOS = StringUtil.nullifyBadInput(submissionTOS);
        this.submissionAttribution = StringUtil.nullifyBadInput(submissionAttribution);
        this.submitterType = StringUtil.nullifyBadInput(submitterType);
        this.submitter = StringUtil.nullifyBadInput(submitter);
        this.signer = StringUtil.nullifyBadInput(signer);
    }
    
    /**
     * Create a new activity with specified details
     * This version of the constructor includes tags
     *
     * @param resourceLocator value for "resource_locator"
     * @param submitter value for "submitter"
     * @param submitterType value for "submitter_type"
     * @param submissionTOS value for "submission_TOS"
     * @param submissionAttribution value for "submission_attribution"
     * @param signer value for "signer"
     * @param tags value for "keys"
     */
    public LRActivity(String resourceLocator, String submitter, String submitterType, String submissionTOS, String submissionAttribution, String signer, String[] tags)
    {
        this.resourceData = new HashMap<String, Object>();
        this.resourceDataType = resourceDataTypeValue;
        this.resourceLocator = StringUtil.nullifyBadInput(resourceLocator);
        this.curator = null;
        this.owner = null;
        this.tags = StringUtil.removeDuplicates(tags);
        this.payloadPlacement = payloadPlacementValue;
        this.payloadSchemaLocator = null;
        this.payloadSchema = payloadSchemaValue;
        this.submissionTOS = StringUtil.nullifyBadInput(submissionTOS);
        this.submissionAttribution = StringUtil.nullifyBadInput(submissionAttribution);
        this.submitterType = StringUtil.nullifyBadInput(submitterType);
        this.submitter = StringUtil.nullifyBadInput(submitter);
        this.signer = StringUtil.nullifyBadInput(signer);
    }
    
    public Object getResourceData()
    {
        Map<String, Object> returnableData = new HashMap<String, Object>();
        Map<String, Object> activityData = new HashMap<String, Object>();
        
        activityData.putAll((HashMap<String, Object>) resourceData);
        
        if (related.size() > 0)
        {
            activityData.put("related", related);
        }
        
        returnableData.put("activity", activityData);
        return returnableData;
    }
    
    /**
     * Add an arbitrary map of key-value pairs as a measure to the verb
     * @param properties
     * @return True if added, false if not (due to missing required fields or lack of "verb")
     */
    public boolean addMeasureToVerb(Map<String, Object> properties)
    {   
        String[] pathKeys = {"verb"};
        return addChild("measure", properties, pathKeys);
    }
    
    
    /**
     * Add a mesure object to the verb within this activity
     *
     * @param measureType The name of the type of measure to be added (required)
     * @param value The value of this measure (required)
     * @param scaleMin The low end of the scale represented
     * @param scaleMax The high end of the scale represented
     * @param sampleSize the number of samples represented by this data
     * @return True if added, false if not (due to missing required fields or lack of "verb")
     */
    public boolean addMeasureToVerb(String measureType, Number value, Number scaleMin, Number scaleMax, Number sampleSize)
    {
        Map<String, Object> container = new HashMap<String, Object>();
        
        if (measureType != null)
        {
            container.put("measureType", measureType);
        }
        else
        {
            return false;
        }
        if (value != null)
        {
            container.put("value", value);
        }
        else
        {
            return false;
        }
        if (scaleMin != null)
        {
            container.put("scaleMin", scaleMin);
        }
        if (scaleMax != null)
        {
            container.put("scaleMax", scaleMax);
        }
        if (sampleSize != null)
        {
            container.put("sampleSize", sampleSize);
        }
        
        return addMeasureToVerb(container);
    }
    
    /**
     * Add a context string to the verb within this activity
     *
     * @param context The string to use as the context
     * @return True if added, false if not (due to lack of "verb")
     */
    public boolean addContextToVerb(String context)
    {
        String[] pathKeys = {"verb"};
        return addChild("context", context, pathKeys);
    }
    
    /**
     * Add a context object to the verb within this activity
     *
     * @param objectType The type of context
     * @param id The id of the context
     * @param description Description of the context
     * @return True if added, false if not (due to lack of "verb")
     */
    public boolean addContextToVerb(String objectType, String id, String description)
    {
        Map<String, Object> container = new HashMap<String, Object>();
        
        if (objectType != null)
        {
            container.put("objectType", objectType);
        }
        if (id != null)
        {
            container.put("id", id);
        }
        if (description != null)
        {
            container.put("description", description);
        }
        
        String[] pathKeys = {"verb"};
        return addChild("context", container, pathKeys);
    }
    
    /**
     * Add a context object to the verb within this activity, with an array of descriptions
     *
     * @param objectType The type of context
     * @param id The id of the context
     * @param description Array of descriptions of the context
     * @return True if added, false if not (due to lack of "verb")
     */
    public boolean addContextToVerb(String objectType, String id, String[] description)
    {
        Map<String, Object> container = new HashMap<String, Object>();
        
        if (objectType != null)
        {
            container.put("objectType", objectType);
        }
        if (id != null)
        {
            container.put("id", id);
        }
        if (description != null && description.length > 0)
        {
            container.put("description", description);
        }
        
        String[] pathKeys = {"verb"};
        return addChild("context", container, pathKeys);
    }
    
    /**
     * Add a verb object to this activity
     *
     * @param action The action type of the verb (required)
     * @param dateStart The start date of the action described
     * @param dateEnd The end date of the action described
     * @param description An array of descriptions of this action
     * @param comment A comment on this verb
     * @return True if added, false if not (due to missing required fields)
     */
    public boolean addVerb(String action, Date dateStart, Date dateEnd, String[] description, String comment)
    {
        Map<String, Object> container = new HashMap<String, Object>();
        
        if (action != null)
        {
            container.put("action", action);
        }
        else
        {
            return false;
        }
        if (dateStart != null && dateEnd != null)
        {
            container.put("date", df.format(dateStart) + "/" + df.format(dateEnd));
        }
        else if (dateStart != null)
        {
            container.put("date", df.format(dateStart));
        }
        if (description != null && description.length > 0)
        {
            container.put("description", description);
        }
        if (comment != null)
        {
            container.put("comment", comment);
        }
        
        return addChild("verb", container, null);
    }
    
    /**
     * Add an actor object to this activity
     *
     * @param objectType The type of actor (required)
     * @param displayName Name of the actor
     * @param url URL of a page representing the actor
     * @param description Array of descriptiosn of this actor
     * @return True if added, false if not (due to missing required fields)
     */
    public boolean addActor(String objectType, String displayName, String url, String[] description)
    {
        Map<String, Object> container = new HashMap<String, Object>();
    
        if (objectType != null)
        {
            container.put("objectType", objectType);
        }
        else
        {
            return false;
        }
        if (displayName != null)
        {
            container.put("displayName", displayName);
        }
        if (url != null)
        {
            container.put("url", url);
        }
        if (description != null && description.length > 0)
        {
            container.put("description", description);
        }
        
        return addChild("actor", container, null);
    }
    
    /**
     * Add an object to this activity
     *
     * @param objectType The type of object (required)
     * @param id Id of the object
     * @param content String describing the content of the object
     * @return True if added, false if not
     */
    public boolean addObject(String objectType, String id, String content)
    {
        Map<String, Object> container = new HashMap<String, Object>();
        
        if (objectType != null)
        {
            container.put("objectType", objectType);
        }
        if (id != null)
        {
            container.put("id", id);
        }
        if (content != null)
        {
            container.put("content", content);
        }
        
        return addChild("object", container, null);
    }
    
    /**
     * Add a related object to this activity
     *
     * @param objectType The type of the object (required)
     * @param id Id of the ojbect
     * @param content String describing the content of the object
     * @return True if added, false if not (due to missing required fields)
     */
    public boolean addRelatedObject(String objectType, String id, String content)
    {
        Map<String, Object> container = new HashMap<String, Object>();
        
        if (objectType != null)
        {
            container.put("objectType", objectType);
        }
        else
        {
            return false;
        }
        if (id != null)
        {
            container.put("id", id);
        }
        if (content != null)
        {
            container.put("content", content);
        }

        related.add(container);
        return true;
    }
    
    /**
     * Add a content string
     *
     * @param content String describing the content of this activity
     * @return True if added, false if not
     */
    public boolean addContent(String content)
    {
        return addChild("content", content, null);
    }
    
    /**
     * Get a map embedded in the activity following a set of keys
     *
     * @param pathKeys An array of keys to follow to get to the requested map
     * @return The requested map or null if it does not exist
     */
    private Map<String, Object> getMap(String[] pathKeys)
    {
        if (pathKeys == null)
        {
            return (Map<String, Object>)resourceData;
        }
        
        Map<String, Object> selected = (Map<String, Object>)resourceData;
    
        for(int i = 0; i < pathKeys.length; i++)
        {
            if (selected.containsKey(pathKeys[i]) && selected.get(pathKeys[i]).getClass().equals(resourceData.getClass()))
            {
                selected = (Map<String, Object>) selected.get(pathKeys[i]);
            }
            else
            {
                return null;
            }
        }
        
        return selected;
    }
    
    /**
     * Add a child object to the activity at the specified path
     *
     * @param name Name of the object to add
     * @param value Object to add
     * @param pathKeys The path in which to add the object
     * @return True if added, false if not (due to bad path or duplicate name at destination)
     */
    private boolean addChild(String name, Object value, String[] pathKeys)
    {
        Map<String, Object> selected = (Map<String, Object>)resourceData;
        
        if (pathKeys != null)
        {
            selected = getMap(pathKeys);
        }
        
        if (selected != null && !selected.containsKey(name))
        {
            selected.put(name, value);
            return true;
        }
        
        return false;
    }
}