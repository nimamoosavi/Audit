#### This package is an interface of Audit Project that implement in Audit Project if you need to this project you must generate a bean From It in your Project and add the project To your Pom File

### Requirements
The library works with Java 8+, ladder framework 1.0.1+

## [Core](https://github.com/nimamoosavi/core/wiki)


### Structure
this class Used For create Log and store in implementation Project such ass kafka and etc.

![Audit](https://github.com/nimamoosavi/Audit/wiki/images/audit.jpg)


### Audit used For log and used the Log annotation in Framework for CrossCutting  Audit Project can log in File, Kafka ( used From Kafka client Project ) , Elastic search ( used From Elastic search client )

### Audit Maven Central
~~~
<dependency>
            <groupId>app.ladderproject</groupId>
            <artifactId>audit</artifactId>
            <version>1.0.2-Release</version>
</dependency>
~~~

> Audit Project is an Implementation Of this package and you can change It with your implementation but you must implement this interface

## Example

~~~
@Service
public class AffairService extends MicroClientService<AffairReqVM, AffairResVM, Long> {
    @Override
    @Log(type = AuditFactory.AuditType.BEFORE)
    public BaseDTO<AffairResVM> create(AffairReqVM affairReqVM) {
        return super.create(affairReqVM);
    }
}
~~~
