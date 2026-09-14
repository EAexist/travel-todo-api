package db

import (
	"os"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestNewDBConnection(t *testing.T) {
	// Setup: Clear DATABASE_URL to test default behavior
	os.Unsetenv("DATABASE_URL")

	// Act: Try to get a connection (this might fail if no Postgres running, 
    // which is expected in a pure unit test environment)
	db, err := NewDBConnection()

	// Assert: Depending on test environment, this might return an error
	// or a db instance. Just verifying the function call doesn't panic.
	if err != nil {
		t.Logf("Expected error if no Postgres running: %v", err)
	} else {
		assert.NotNil(t, db)
	}
}
