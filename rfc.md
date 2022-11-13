# [RFC Template] Title

**Authors:**

- @nickname
- @nickname

## 1 Summary

This is the RFC document of the Ourplace chat-service system design proposal

## 2 Motivation

## 3. Requirements

### Functional requirements

- The chat-service should support group chat in the beginning
- It can support 1-on-1 chat in a later stage
- The service should be centered around a mobile app
- It should also have a web app
- Most important features are:
    - group chat
    - support only text messages in the beginning
    - support for standard & animated emojis, polls and events can be added later
    - support for audio messages and audio group calls can be added much later
- Indicating online presence is not business critical. Can be considered later
- It should limit a user to login on a single mobile device and web client at the same time. Similar to Whatsapp
- It should push notifications to users.
- User can send and delete a message she has formed
- User add add new chat rooms (called as "places" from now onwards)
- User can remove a place she owns if no other user is inside
- User can invite people to a place
- User can enter and exit a place
- User can receive messages from her places
- User can receive notifications from her places
- User can mute/unmute notifications from a place
- System only supports public places in the beginning
- Ssystem can support private places in a later stage

### Functional limits

- A user can belong up to 100 (places)
- Text message size can be limited to 500 characters
- Media message size can be limited to 5MB
- Chat history should be saved for 3 months
- Chat history can be stored forever in a later stage
- Message transfer can be unencrypted in the beginning
- End-to-end encyrption can be added later
- It should be a startup company app (no high-pressure in the beginning):
    - few simple features
    - simple UI
    - It should support 10K daily active users (DAU) in the beginning.
    - When it needs to scale to 1M DAU, it should be elastic enough to horizontally scale with few modifications and not with many rewrites
    - It should aim to auto-scale in the long run

### Non-functional requirements

- It should have low delivery latency 
- System data should be durable and accurate
    - real-time messages should not get lost
    - messages should be accurate
        - correct content, user, date, time, order
        - no duplicates
    - message history should persist for at least 3 months
    - global chat roomms (places) and users' chat rooms (places) should persist until they are deleted
- The system should avoid single point of failure from the beginning
- It should respond reasonbly and handle failover in cases like:
    - server failure
    - network loss
    - deviance in network latency
    - network partitioning
    - data center outage
    - third-party push notification service failure
- It should benefit from tools to easily aggregate, search and view system logs
- It should be possible to collect system health metrics in an early stage such as:
    - host level cpu, memory, disk i/o usage
    - aggregated metrics on DB and caching performance
    - queued messages to be pushed/published
- It should be possible to collect business metrics in a later stage related to:
    - active users
    - message counts
    - user retention
    - revenue
- It should be easily and repeatedly deployable to the cloud
- It should have test and prod environments
- It should have automated tooling ready in an early stage for:
    - CI; build, test and merge
    - deploy
- The system needs to minimize costs in the beginning:
    - hardware costs
    - operational support costs
- The significance of the aggregated cost can go down in later stages
- Cost/DAU ratio is always significant
- Due to low resources, it should benefit from "[boring technologies](https://boringtechnology.club/)" and few technologies (try to reuse existing tech to solve multiple problems)

### Some questions to answer in terms of system requirements

- What are the goals and the feature scope of the chat-service?
    - Do we support features other than chat related?
- How many users are estimated to use the service?
    - early phase?
    - growth phase?
- What are the anticipated scales in 3 months, 6 months and a year?
    - what is the anticipated traffic volume?
    - how should the system handle the next scale curve
        - e.g. vertical scaling, rewrite, auto-scaling, something in between?
- What is the current tech stack know-how? 
- Can we leverage the existing know-how to build some of the system components?
- What kind of UIs?
    - web?
    - mobile?
- Do we support images, audio, video messaging besides text?
- For how long shoud we store messages?
- How many chat rooms (places) can a user belong to?
- How to reduce latency? Make the messaging as fast as possible.
- How to manage online/offline status?
- How should the system respond to error cases?
    - server failures
    - network losses
    - deviance in network latency
    - network partitionings
    - data center outage
- What is important to measure?
- How should we roll out the service?
- Do we need a staging environment besides production?
- What about data
    - should we compress data?
        - when? before sending, before storing?

## 4 Proposed Implementation

- serve static content from edge locations (CDN)

- consider multiple data centers, make use of geoDNS-routed traffic somehow, increase availablity and reduce latency
    - think about tradeoffs, how to manage the consequences of the replication?

- load balancer to distribute traffic to web servers

- stateless web server
    - To make the system resilient, easier to implement and scale (autoscalling is easy when we don't need to manage and replicate state data in the web tier):
        - stateless web server (avoid stick sessions)
        - state stored in a shared data store (sql or nosql db)
        - e.g. user session data stored in the persistent data store (memcached/redis -> NoSQL is easier to scale)

- consider message queues
    - systems that store data in memory
    - to decouple system components and scale independently
    - add asyncronous nature to the data flow
    - think of web servers as producers and "workers" as consumers
        - what could a worker do in our scenario?
            workers can pull messages from the queue send it to third-party services (APNs and FCM). Third-party services can then send messages as push notification to user (IOS and Android) devices.
    - consider using Redis for 3 purposes; message queue, caching system and shared data storage.

- caching frequently access data
    - make the web tier fetch certain data from the cache (memcached/redis -> NoSQL is easier to Scale, key-value store makes sense for a caching system)

- master and slave db setup
    - distribute writes and reads smartly
    - use sharding over vertical scaling
        - 

- shared data store for user state information (can be the same tech as the caching choice)

- system monintoring
    - logging: aggregate logs using a centralized service
        easily search and view logs
    - metrics:
        - system health status
            - host level cpu, memory, disk i/o
            - aggregated metrics on database tier and cache tier peformance
        - gain business insights
            - daily active users
            - user retention
            - revenue

- automation tooling
    - ci
        - build, test and merge
    - deploy


## 5 Metrics & Dashboards

## 6 Drawbacks

## 7 Alternatives

## 8 Potential Impact and Dependencies

## 9 Unresolved questions

## 10 Conclusion

## 11 RFC Process Guide, remove this section when done

*By writing an RFC, you're giving insight to your team on the direction you're taking. There may not be a right or better decision in many cases, but we will likely learn from it. By authoring, you're making a decision on where you want us to go and are looking for feedback on this direction from your team members, but ultimately the decision is yours.*

This document is a:

- thinking exercise, prototype with words.
- historical record, its value may decrease over time.
- way to broadcast information.
- mechanism to build trust.
- tool to empower.
- communication channel.

This document is not:

- a request for permission.
- the most up to date representation of any process or system

**Checklist:**

- [ ]  Copy template
- [ ]  Draft RFC (think of it as a wireframe)
- [ ]  Share as WIP with folks you trust to gut-check
- [ ]  Send pull request when comfortable
- [ ]  Label accordingly
- [ ]  Assign reviewers (ask your manager if in doubt)
- [ ]  Merge yourself with two approved reviews

**Recommendations**

- Tag RFC title with [WIP] if you're still ironing out details.
- Tag RFC title with [newbie] if you're trying out something experimental or you're not entirely convinced of what you're proposing.
- Tag RFC title with [SWARCH] if you'd like to schedule a SWARCH review to discuss the RFC.
- If there are areas that you're not convinced on, tag people who you consider may know about this and ask for their input.
- If you have doubts, ask your manager for help moving something forward.
- As the author/s, this is _your decision_. You are empowered to choose to move forward despite dissenting comments. We're not looking for consensus-driven decision-making.
- The success of the implementation of your proposal depends on how this decision relates to our company's objectives and priorities.