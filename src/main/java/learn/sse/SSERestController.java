package learn.sse;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseBroadcaster;
import javax.ws.rs.sse.SseEventSink;

@Path("/")
public class SSERestController {

    private Sse sse;
    private SseBroadcaster sseBroadcaster;
    private OutboundSseEvent.Builder eventBuilder;

    @Context
    public void setSse(Sse sse) {
        this.sse = sse;
        this.eventBuilder = sse.newEventBuilder();
        this.sseBroadcaster = sse.newBroadcaster();
    }

    @GET
    @Path("/updates")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void publishUpdates(@Context SseEventSink sseEventSink, @HeaderParam(HttpHeaders.LAST_EVENT_ID_HEADER)
    @DefaultValue("-1") int lastReceivedId) {
		/*lastEventId++;
		Map<String, String> newTime = new HashMap<>();
		newTime.put("currentTime", LocalDateTime.now().toString());
		String json = "{\"currentTime\": \""+LocalDateTime.now().toString()+"\""+"}";
		OutboundSseEvent outboundSseEvent;
		try {
			outboundSseEvent = m_sse.newEventBuilder().name("time-update").id(String.valueOf(lastEventId))
					.comment("see time update").data(json+"\n\n")
					.reconnectDelay(5000).mediaType(MediaType.APPLICATION_JSON_TYPE).build();
		}catch (Exception e) {
			log.error(e);
			throw new RuntimeException(e);
		}
		eventSink.send(outboundSseEvent);
		eventSink.close();*/
		/*int lastEventId = 0;
		if (lastReceivedId != -1) {
			lastEventId = ++lastReceivedId;
		}
		boolean running = true;
		while (running) {
			String json = "{\"currentTime\": \""+LocalDateTime.now().toString()+"\""+"}";
				OutboundSseEvent sseEvent = this.eventBuilder
						.name("time")
						.id(String.valueOf(lastEventId))
						.mediaType(MediaType.APPLICATION_JSON_TYPE)
						.data(json)
						.reconnectDelay(3000)
						.comment("time updates")
						.build();
				sseEventSink.send(sseEvent);
				lastEventId++;
			//Simulate connection close
			if (lastEventId % 5 == 0) {
				sseEventSink.close();
				break;
			}

			try {
				//Wait 5 seconds
				Thread.sleep(5 * 1000);
			} catch (InterruptedException ex) {
				// ...
			}
			//Simulatae a while boucle break
			running = lastEventId <= 2000;
		}
		sseEventSink.close();*/
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final OutboundSseEvent event = sse.newEventBuilder()
                        .name("update-message")
                        .data(String.class, "Update " + i + "!")
                        .build();
                sseEventSink.send(event);
            }
        }).start();
    }

    @GET
    @Path("/subscribe")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void subscribe(@Context SseEventSink sseEventSink) {
        sseEventSink.send(sse.newEvent("Welcome !"));
        System.out.println("User subscribed...");
        this.sseBroadcaster.register(sseEventSink);
        sseEventSink.send(sse.newEvent("You are registred !"));
    }

  /*  @GET
    @Path("subscribe")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void listen(@Context SseEventSink sseEventSink) {
        sseEventSink.send(sse.newEvent("Welcome !"));
        this.sseBroadcaster.register(sseEventSink);
        sseEventSink.send(sse.newEvent("You are registred !"));
    }*/

    @GET
    @Path("/broadcast")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void broadcast() {

        new Thread(() -> {
            int i = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final OutboundSseEvent event = sse.newEventBuilder()
                        .name("broadcast-message")
                        .data(String.class, "Broadcast " + (i++) + "!")
                        .build();
                this.sseBroadcaster.broadcast(event);
            }
        }).start();
    }
}
