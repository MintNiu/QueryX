# QueryX
QueryX 是一个基于 MyBatis Plus 的查询增强框架。  通过 @Eq、@Like、@In、@Between 等注解，可以直接在 Query DTO 中定义查询规则，自动生成 LambdaQueryWrapper，减少大量重复的条件拼装代码。  目标是让开发者专注于业务，而不是编写重复的查询逻辑。

QueryX is a lightweight query enhancement framework built on top of MyBatis Plus.
By using simple annotations such as @Eq, @Like, @In, and @Between, developers can define query conditions directly in DTOs without manually building LambdaQueryWrapper objects.
QueryX focuses on reducing repetitive CRUD code, improving readability, and accelerating backend development.

UserQuery
      ↓
   QueryX
      ↓
LambdaQueryWrapper
      ↓
   SQL
