create table rule_failure_log
(
  id              serial primary key,
  form_id         varchar(255) not null,
  rule_type       varchar(255) not null,
  entity_type     varchar(255) not null,
  entity_id       varchar(255) not null,
  error_message   varchar(255) not null,
  stacktrace      text         not null,
  source          varchar(255) not null,
  audit_id        integer
);