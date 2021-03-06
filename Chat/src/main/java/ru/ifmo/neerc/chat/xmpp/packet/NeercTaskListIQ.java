package ru.ifmo.neerc.chat.xmpp.packet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.jivesoftware.smack.packet.IQ;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import ru.ifmo.neerc.task.Task;
import ru.ifmo.neerc.task.TaskStatus;
import ru.ifmo.neerc.utils.XmlUtils;

/**
 * @author Dmitriy Trofimov
 */
public class NeercTaskListIQ extends NeercIQ {
	private Collection<Task> tasks = new ArrayList<Task>();

	public NeercTaskListIQ() {
		super("tasks");
	}

	public Collection<Task> getTasks() {
		return Collections.unmodifiableCollection(tasks);
	}
 
	public void addTask(Task task) {
		tasks.add(task);
	}

    @Override
    protected IQ.IQChildElementXmlStringBuilder getIQChildElementBuilder(IQ.IQChildElementXmlStringBuilder xml) {
        xml.rightAngleBracket();

		for (Task task : tasks) {
            xml.halfOpenElement("task");
            xml.attribute("title", task.getTitle());
            xml.attribute("type", task.getType());
            xml.attribute("id", task.getId());
            xml.rightAngleBracket();

			for (Map.Entry<String, TaskStatus> entry : task.getStatuses().entrySet()) {
                xml.halfOpenElement("status");
                xml.attribute("for", entry.getKey());
                xml.attribute("type", entry.getValue().getType());
                xml.optAttribute("value", entry.getValue().getValue());
                xml.closeEmptyElement();
			}

            xml.closeElement("task");
		}

        return xml;
	}

    @Override
	public void parse(XmlPullParser parser) throws XmlPullParserException, IOException {
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("task")) {
					addTask(parseTask(parser));
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("query")) {
					done = true;
				}
			}
		}
    }

	public static Task parseTask(XmlPullParser parser) throws XmlPullParserException, IOException {
		Date date = new Date();
    	String timestamp = parser.getAttributeValue("", "timestamp");
    	if (timestamp != null) {
    		date = new Date(Long.parseLong(timestamp));
    	}
		Task task = new Task(
			parser.getAttributeValue("", "id"),
			parser.getAttributeValue("", "type"),
			parser.getAttributeValue("", "title"),
			date
		);
		boolean done = false;
		while (!done) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.START_TAG) {
				if (parser.getName().equals("status")) {
					task.setStatus(
						parser.getAttributeValue("", "for"),
						parser.getAttributeValue("", "type"),
						parser.getAttributeValue("", "value")
					);
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				if (parser.getName().equals("task")) {
					done = true;
				}
			}
		}
		return task;
	}
}
