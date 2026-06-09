ALTER TABLE sys_user
  ADD COLUMN position_name VARCHAR(50) NULL AFTER department_id;

UPDATE sys_user
SET position_name = CASE id
  WHEN 1 THEN '平台负责人'
  WHEN 2 THEN '运维负责人'
  WHEN 3 THEN '产品经理'
  WHEN 4 THEN '研发工程师'
  ELSE position_name
END;
