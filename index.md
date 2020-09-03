---
title: "JCR Configuration"
addon: "JCR"
repo: "https://github.com/seedstack/jcr-addon"
author: Xian BOULLOSA
description: "Provides configuration and injection for Java Repository Content API."
tags:
    - jcr
    - content repository
    - injection
zones:
    - Addons
menu:
    JCR:
        parent: "contents"
        weight: 10
noMenu: true    
---

SeedStack JCR add-on supports any JCR-compliant [Repository Content](https://en.wikipedia.org/wiki/Content_repository_API_for_Java) to allow
your application to interface with a repository content.<!--more-->

## Dependencies

{{< dependency g="org.seedstack.addons.jcr" a="jcr" >}}
{{% tabs list="Jackrabbit" %}}
{{% tab "Jackrabbit" true %}}
Jackrabbit is the de-facto JCR implementation. When using Hibernate, SeedStack is able to stream results from the database 
without putting them all in memory (useful when retrieving for result sets).

{{< dependency g="org.apache.jackrabbit" a="jackrabbit-core">}}
{{% /tab %}}
{{% /tabs %}}

## Configuration

SeedStack is able to automatically detect the JPA classes in your project, without `persistence.xml` file. You just have 
to declare the JPA units:

{{% config p="jcr" %}}
```yaml
jcr:
  # Configured JCR repositories with the name of the JCR repository as key.
  repositories:
    myRepository:
      # Type of JCR Connection
      type: (JNDI_NAME|JNDI_URI|LOCAL_PATH|REMOTE_URI)
      # Address to the JCR Server, it could be a local path, a remote server, or a JNDI Resource (Type must match the url)
      address: (String)
      # JCR Username
      username: (String)
      # JCR Password
      password: (String)
      # The fully qualified class name of the jcr factory (Optional)
      repositoryFactory: (Class<? extends JcrFactory>)
      # The Provider Properties
      vendorProperties:
        prop1: value
  # Default repository to use when not specified
  defaultRepository: (String)
```

{{% /config %}}

## Usage

To use the Entity Manager directly, simply inject it:

```java
import javax.jcr.Session;

public class MyService {
    @Inject
    private Session myJcrSession;
    
    @WithContentRepository
    @Named("myRepository") // Optional if default repository is specified
    public void doSomethingWithMyJcrRepository() {
        // do something
    }
}
```

{{% callout info %}}
All JCR Interactions should happen within the limits of a @WithContentRepository.
Also, Sessions are automatically managed by the stack, there's no need to close them.
{{% /callout %}}
