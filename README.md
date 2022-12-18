# MarketBill Core Server
: graphql, spring boot


## Performance
### Receipt Process

- Problem
  - Receipt process at first time too slow
    - <img width="128" alt="image" src="https://user-images.githubusercontent.com/37768791/208289020-1e654c0c-d215-4006-aec9-955af0418d9c.png">

- Cause : Aws Lambda cold start 
- Solve(candidates)
  - Set Aws Lambda Provisioned Concurrency
  - Set Lambda ping scheduler(GET http request per 1 minute to lambda)


## References
- Aws codebuild -> ecr 
  - https://yunsangjun.github.io/cloud/2019/06/21/aws-cicd03.html
- Spring Boot + ECS + ALB(application load balancer) restart issue
  - https://stackoverflow.com/questions/67405335/aws-ecs-fargate-tasks-with-spring-boot-caught-in-restart-loop-how-to-configure
- Load Balancer forward issue
  - https://www.youtube.com/watch?v=o7s-eigrMAI
codebuild test용 주석2
- DGS pagination in dataloader with context
  - https://medium.com/fiverr-engineering/why-you-should-not-pass-input-parameters-using-dgs-custom-context-d1337fc0bcae