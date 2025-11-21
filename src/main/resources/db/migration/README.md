# Database Migrations - Flyway

Este diretório contém as migrações do banco de dados usando Flyway.

## Estrutura de Nomenclatura

- **Versionadas**: `V{versão}__{descrição}.sql`
  - Exemplo: `V1__Create_person_table.sql`
  - Aplicadas apenas uma vez em ordem sequencial

- **Repetíveis**: `R__{descrição}.sql`
  - Aplicadas sempre que o conteúdo muda
  - Úteis para views, procedures, functions

## Migrações Existentes

- **V1__Create_person_table.sql**: Criação da tabela person com índices
- **V2__Insert_sample_data.sql**: Dados de exemplo para desenvolvimento

## Comandos Úteis

### Verificar status das migrações
```bash
mvn flyway:info
```

### Aplicar migrações pendentes
```bash
mvn flyway:migrate
```

### Limpar banco (CUIDADO!)
```bash
mvn flyway:clean
```

### Validar migrações
```bash
mvn flyway:validate
```

## Configuração

As configurações do Flyway estão em `application.yaml`:
- **url**: Conexão JDBC com PostgreSQL (Neon)
- **locations**: `classpath:db/migration`
- **baseline-on-migrate**: `true` (para bancos existentes)
- **validate-on-migrate**: `true` (validação automática)

## Boas Práticas

1. **Nunca modifique** migrações já aplicadas em produção
2. **Use transações** implícitas (Flyway gerencia automaticamente)
3. **Teste localmente** antes de aplicar em outros ambientes
4. **Backup sempre** antes de migrações em produção
5. **Use nomes descritivos** para as migrações