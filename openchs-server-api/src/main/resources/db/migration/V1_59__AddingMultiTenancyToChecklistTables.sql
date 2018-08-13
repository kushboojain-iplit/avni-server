CREATE POLICY checklist_detail_org ON checklist_detail USING (organisation_id IN (WITH RECURSIVE list_of_orgs(id, parent_organisation_id) AS ( SELECT id, parent_organisation_id FROM organisation WHERE db_user = current_user UNION ALL SELECT o.id, o.parent_organisation_id FROM organisation o, list_of_orgs log WHERE o.id = log.parent_organisation_id ) SELECT id FROM list_of_orgs)) WITH CHECK ( organisation_id IN (WITH RECURSIVE list_of_orgs(id, parent_organisation_id) AS (SELECT id, parent_organisation_id FROM organisation WHERE db_user = current_user UNION ALL SELECT o.id, o.parent_organisation_id FROM organisation o, list_of_orgs log WHERE o.id = log.parent_organisation_id ) SELECT id FROM list_of_orgs));
CREATE POLICY checklist_item_detail_org ON checklist_item_detail USING (organisation_id IN (WITH RECURSIVE list_of_orgs(id, parent_organisation_id) AS ( SELECT id, parent_organisation_id FROM organisation WHERE db_user = current_user UNION ALL SELECT o.id, o.parent_organisation_id FROM organisation o, list_of_orgs log WHERE o.id = log.parent_organisation_id ) SELECT id FROM list_of_orgs)) WITH CHECK ( organisation_id IN (WITH RECURSIVE list_of_orgs(id, parent_organisation_id) AS (SELECT id, parent_organisation_id FROM organisation WHERE db_user = current_user UNION ALL SELECT o.id, o.parent_organisation_id FROM organisation o, list_of_orgs log WHERE o.id = log.parent_organisation_id ) SELECT id FROM list_of_orgs));
ALTER TABLE checklist_detail ENABLE ROW LEVEL SECURITY;
ALTER TABLE checklist_item_detail ENABLE ROW LEVEL SECURITY;