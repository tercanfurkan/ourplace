# [RFC Template] Title

**Authors:**

- @nickname
- @nickname

## 1 Summary

*A short paragraph or bullet list that quickly explains what you're trying to do.*

## 2 Motivation

*What motivates this proposal, and why is it important?*

*Here, we aim to get comfortable articulating the value of our actions.*

## 3. Requirements

*AKA success criteria. Briefly (one or two sentences) state your solution, and then talk about all the good stuff stakeholders will get once the proposal is implemented - impact of the solution. This section is a counterpart to the Motivation section and the same guidelines apply.

*The requirements should make it very clear when the your goal has been achieved.

**Bad: “We’ll make the /do-something endpoint much faster”
**Good: “We’ll achieve a < 100ms response time (99th) for the /do-something endpoint”

*Talk about the things your proposal won’t do. This will prevent scope creep.


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

*This is the core of your proposal, and its purpose is to help you think through the problem because [writing is thinking](https://medium.learningbyshipping.com/writing-is-thinking-an-annotated-twitter-thread-2a75fe07fade).*

*Consider:*

- *using diagrams to help illustrate your ideas.*
- *including code examples if you're proposing an interface or system contract.*
- *linking to project briefs or wireframes that are relevant.*

## 5 Metrics & Dashboards

*What are the main metrics we should be measuring? For example, when interacting with an external system, it might be the external system latency. When adding a new table, how fast would it fill up?*

## 6 Drawbacks

*Are there any reasons why we should not do this? Here we aim to evaluate risk and check ourselves.*

## 7 Alternatives

*What are other ways of achieving the same outcome?*

## 8 Potential Impact and Dependencies

*Here, we aim to be mindful of our environment and generate empathy towards others who may be impacted by our decisions.*

- *What other systems or teams are affected by this proposal?*
- *How could this be exploited by malicious attackers?*

## 9 Unresolved questions

*What parts of the proposal are still being defined or not covered by this proposal?*

## 10 Conclusion

*Here, we briefly outline why this is the right decision to make at this time and move forward!*

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