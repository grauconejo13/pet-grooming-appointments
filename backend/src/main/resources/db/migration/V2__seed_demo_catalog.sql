INSERT INTO groomers (first_name, last_name, email, phone, active)
VALUES
    ('Maya', 'Chen', 'maya@pawandpolish.demo', '210-555-0101', TRUE),
    ('Alex', 'Rivera', 'alex@pawandpolish.demo', '210-555-0102', TRUE),
    ('Sam', 'Patel', 'sam@pawandpolish.demo', '210-555-0103', TRUE)
ON CONFLICT (email) DO NOTHING;

INSERT INTO grooming_services (name, description, duration_minutes, price, active)
VALUES
    ('Full Groom', 'Bath, brush, haircut, nail trim, and ear clean.', 90, 85.00, TRUE),
    ('Freshen Up', 'Bath, blow dry, brush out, and nail trim.', 60, 55.00, TRUE),
    ('Puppy First Visit', 'A gentle introduction to grooming for first-time puppies.', 45, 40.00, TRUE)
ON CONFLICT (name) DO NOTHING;
