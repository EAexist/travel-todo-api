package db

import (
	"os"
	"time"

	"gorm.io/driver/postgres"
	"gorm.io/gorm"
)

func NewDBConnection() (*gorm.DB, error) {
	dsn := os.Getenv("DATABASE_URL")
	if dsn == "" {
		dsn = "host=localhost user=postgres password=postgres dbname=postgres port=5432 sslmode=disable"
	}

	db, err := gorm.Open(postgres.Open(dsn), &gorm.Config{})
	if err != nil {
		return nil, err
	}

	sqlDB, err := db.DB()
	if err != nil {
		return nil, err
	}

	// 60 Open / 30 Idle gives enough room for 40 workers + background assertions
	sqlDB.SetMaxOpenConns(100)
	sqlDB.SetMaxIdleConns(30)
	sqlDB.SetConnMaxLifetime(5 * time.Minute)

	return db, nil
}
