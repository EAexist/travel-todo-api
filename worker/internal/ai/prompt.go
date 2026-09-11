package ai

import (
	"fmt"
)

// Define basic prompt templates
const (
	ReservationExtractionTemplate = `
텍스트에서 예약 내역에 관한 내용과 링크를 포함한 부분들을 수정없이 추출하고 합쳐. 그리고 모든 예약 내역을 추출해.
Text: %s
`
)

// GenerateReservationPrompt constructs the prompt based on the template
func GenerateReservationPrompt(rawText string) string {
	return fmt.Sprintf(ReservationExtractionTemplate, rawText)
}
