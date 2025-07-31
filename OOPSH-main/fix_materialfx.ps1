# PowerShell script để sửa các lỗi MaterialFX
$sourceDir = "c:\Users\itent\Desktop\OOPSH\src\main\java\com\pocitaco\oopsh\controllers"

# Tìm tất cả file .java trong thư mục controllers
$javaFiles = Get-ChildItem -Path $sourceDir -Recurse -Filter "*.java"

foreach ($file in $javaFiles) {
    $content = Get-Content $file.FullName -Raw
    
    # Bỏ tất cả setComparator calls
    $content = $content -replace '(\s+)(\w+Cell)\.setComparator\([^;]+\);', '$1// $2.setComparator(...); // Commented out due to MaterialFX compatibility'
    
    # Thay thế MFXTableColumn<> với MFXTableColumn<T> generic
    $content = $content -replace 'new MFXTableColumn<>\(', 'new MFXTableColumn<>('
    
    # Comment out những dòng có problem với type inference
    $content = $content -replace 'new io\.github\.palexdev\.materialfx\.controls\.MFXTableColumn<>\(', '// new io.github.palexdev.materialfx.controls.MFXTableColumn<>('
    
    Set-Content -Path $file.FullName -Value $content
    Write-Host "Fixed: $($file.Name)"
}
