# rebalancing-kafka-listner
Weird requirement but hey nothing's impossible - Someone asked me if I can build a Kafka listener service that is intelligent enough to automatically that a new Topic was created on Kafka service and immediately start consuming from new topic, at the same time delete Topic should delete topic and stop listener on that topic


To request creation and deletion of new topic, I have a restful endpoint /publisher
Based on the event message application will either create a new topic or delete an existing topic

## Creation of new topic 
1. Using Kafka Admin api's, we create a new TOPIC
2. We stop the already running Listeners.
3. We start all the listeners also with a new listener for the new topic.

## Deletion of existing topic and stop listening to this topic:
1. We cannot delete a topic which has active listeners, and so we first stop listener.
2. We delete topic 
3. We start all the listeners except but the topic that has been deleted.

The topic names to be listened are not supplied manually - It's all programmatic we get the list of topic names on kafka using kafka admin endpoint.

August 30 2023