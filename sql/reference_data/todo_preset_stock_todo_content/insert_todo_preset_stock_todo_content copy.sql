INSERT INTO todo_preset_stock_todo_content 
    ("todo-preset_id", "stock-todo-content_id", "is_flagged_to_add") 

SELECT 
    tp.id AS "todo-preset_id",
    stc.id AS "stock-todo-content_id",
    CASE 
        WHEN tp.type = 'DEFAULT' AND stc.type = 'accomodation' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'passport' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'cash' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'prepaid_card' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'credit_card' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'roaming' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'visit_japan' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'military_approvement' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'insurance' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'bag' THEN TRUE
        WHEN tp.type = 'DEFAULT' AND stc.type = 'toothbrush_set' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'cosmetics' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'sunscreen' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'power_bank' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'adapter' THEN TRUE
        WHEN tp.type = 'DEFAULT' AND stc.type = 'charger' THEN TRUE
        WHEN tp.type = 'DEFAULT' AND stc.type = 'earphones' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'suitcase' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'umbrella' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'sunglasses' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'personal_medication' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'towel' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'camera' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'pajamas' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'tissue' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'tumbler' THEN TRUE
        WHEN tp.type = 'DEFAULT' AND stc.type = 'clothing' THEN TRUE
        -- WHEN tp.type = 'DEFAULT' AND stc.type = 'sandals' THEN TRUE
        
        WHEN tp.type = 'DOMESTIC' AND stc.type = 'accomodation' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'passport' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'cash' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'prepaid_card' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'credit_card' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'roaming' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'visit_japan' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'military_approvement' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'insurance' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'bag' THEN TRUE
        WHEN tp.type = 'DOMESTIC' AND stc.type = 'toothbrush_set' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'cosmetics' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'sunscreen' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'power_bank' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'adapter' THEN TRUE
        WHEN tp.type = 'DOMESTIC' AND stc.type = 'charger' THEN TRUE
        WHEN tp.type = 'DOMESTIC' AND stc.type = 'earphones' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'suitcase' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'umbrella' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'sunglasses' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'personal_medication' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'towel' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'camera' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'pajamas' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'tissue' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'tumbler' THEN TRUE
        WHEN tp.type = 'DOMESTIC' AND stc.type = 'clothing' THEN TRUE
        -- WHEN tp.type = 'DOMESTIC' AND stc.type = 'sandals' THEN TRUE
        
        WHEN tp.type = 'FOREIGN' AND stc.type = 'accomodation' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'passport' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'cash' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'prepaid_card' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'credit_card' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'roaming' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'visit_japan' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'military_approvement' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'insurance' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'bag' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'toothbrush_set' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'cosmetics' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'sunscreen' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'power_bank' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'adapter' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'charger' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'earphones' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'suitcase' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'umbrella' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'sunglasses' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'personal_medication' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'towel' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'camera' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'pajamas' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'tissue' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'tumbler' THEN TRUE
        WHEN tp.type = 'FOREIGN' AND stc.type = 'clothing' THEN TRUE
        -- WHEN tp.type = 'FOREIGN' AND stc.type = 'sandals' THEN TRUE
        
        WHEN tp.type = 'JAPAN' AND stc.type = 'accomodation' THEN TRUE
        WHEN tp.type = 'JAPAN' AND stc.type = 'passport' THEN TRUE
        WHEN tp.type = 'JAPAN' AND stc.type = 'cash' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'prepaid_card' THEN TRUE
        WHEN tp.type = 'JAPAN' AND stc.type = 'credit_card' THEN TRUE
        WHEN tp.type = 'JAPAN' AND stc.type = 'roaming' THEN TRUE
        WHEN tp.type = 'JAPAN' AND stc.type = 'visit_japan' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'military_approvement' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'insurance' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'bag' THEN TRUE
        WHEN tp.type = 'JAPAN' AND stc.type = 'toothbrush_set' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'cosmetics' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'sunscreen' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'power_bank' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'adapter' THEN TRUE
        WHEN tp.type = 'JAPAN' AND stc.type = 'charger' THEN TRUE
        WHEN tp.type = 'JAPAN' AND stc.type = 'earphones' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'suitcase' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'umbrella' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'sunglasses' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'personal_medication' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'towel' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'camera' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'pajamas' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'tissue' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'tumbler' THEN TRUE
        WHEN tp.type = 'JAPAN' AND stc.type = 'clothing' THEN TRUE
        -- WHEN tp.type = 'JAPAN' AND stc.type = 'sandals' THEN TRUE
        
        ELSE FALSE 
    END AS "is_flagged_to_add"
FROM 
    todo_preset tp
CROSS JOIN 
    stock_todo_content stc
ON CONFLICT DO NOTHING;