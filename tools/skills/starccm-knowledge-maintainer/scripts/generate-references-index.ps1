[CmdletBinding()]
param(
    [string]$ReferencesRoot,
    [string]$OutputPath
)

$ErrorActionPreference = 'Stop'

# Resolve paths from this script so the repository can be cloned anywhere.
$RepositoryRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..\..\..'))
if ([string]::IsNullOrWhiteSpace($ReferencesRoot)) {
    $ReferencesRoot = Join-Path $RepositoryRoot 'references'
}
if ([string]::IsNullOrWhiteSpace($OutputPath)) {
    $OutputPath = Join-Path $ReferencesRoot 'STARCCM_References_Index.csv'
}

$categories = [ordered]@{
    'Basic_Syntax' = '基础语法'
    'Standalone_STARCCM_Functions' = 'STAR-CCM+ 内部宏功能'
    'External_STARCCM_Runners' = '外部调用 STAR-CCM+'
    'Shared_STARCCM_Libraries' = '共享 STAR-CCM+ 库'
}

$excludedDirectoryNames = @(
    '.git', '.gradle', 'node_modules', 'bin', 'build', 'dist', 'target',
    'out', '__pycache__', '.idea', '.vscode'
)

function Get-IndexedFiles {
    param([System.IO.DirectoryInfo]$Directory)

    Get-ChildItem -LiteralPath $Directory.FullName -Recurse -File -Force |
        Where-Object {
            $parts = $_.FullName.Substring($Directory.FullName.Length).Split([IO.Path]::DirectorySeparatorChar)
            -not ($parts | Where-Object { $excludedDirectoryNames -contains $_ })
        }
}

function Get-Description {
    param([System.IO.FileSystemInfo]$DirectoryItem)

    $readme = if ($DirectoryItem.PSIsContainer) {
        Get-ChildItem -LiteralPath $DirectoryItem.FullName -File -Force -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -ieq 'README.md' } | Select-Object -First 1
    }
    else { $null }

    $agents = if ($DirectoryItem.PSIsContainer) {
        Get-ChildItem -LiteralPath $DirectoryItem.FullName -File -Force -ErrorAction SilentlyContinue |
            Where-Object { $_.Name -ieq 'AGENTS.md' } | Select-Object -First 1
    }
    else { $null }

    $source = if ($readme) { $readme } elseif ($agents) { $agents } else { $null }
    if ($source) {
        $lines = Get-Content -LiteralPath $source.FullName -Encoding UTF8
        $candidate = $lines |
            ForEach-Object { $_.Trim() } |
            Where-Object {
                $_ -and $_ -notmatch '^```' -and $_ -notmatch '^#' -and
                $_ -notmatch '^[-*] ' -and $_ -notmatch '^\|' -and
                $_ -notmatch '^```mermaid'
            } |
            Select-Object -First 1
        if ($candidate) { return ($candidate -replace '\s+', ' ').Trim() }
    }

    if (-not $DirectoryItem.PSIsContainer) {
        if ($DirectoryItem.Extension -ieq '.java') {
            $text = Get-Content -LiteralPath $DirectoryItem.FullName -Raw -Encoding UTF8
            $comment = [regex]::Match($text, '(?s)/\*\*?(.*?)\*/')
            if ($comment.Success) {
                $candidate = ($comment.Groups[1].Value -split "`r?`n" |
                    ForEach-Object { $_ -replace '^\s*\*\s?', '' -replace '\s+', ' ' } |
                    Where-Object { $_.Trim() -and $_.Trim() -notmatch '^@' } |
                    Select-Object -First 1).Trim()
                if ($candidate) { return $candidate }
            }
        }
        return "文件：$($DirectoryItem.Name)"
    }

    return "目录：$($DirectoryItem.Name)"
}

function New-IndexRow {
    param(
        [string]$CategoryName,
        [string]$CategoryLabel,
        [System.IO.FileSystemInfo]$Item
    )

    $isDirectory = $Item.PSIsContainer
    $files = if ($isDirectory) { @(Get-IndexedFiles -Directory $Item) } else { @($Item) }
    $relative = [IO.Path]::GetRelativePath($ReferencesRoot, $Item.FullName).Replace('\', '/')
    $java = @($files | Where-Object Extension -ieq '.java').Count
    $scripts = @($files | Where-Object { $_.Extension -in @('.ps1', '.py', '.sh', '.bat', '.cmd', '.mjs', '.js') }).Count
    $docs = @($files | Where-Object { $_.Extension -in @('.md', '.txt', '.rst', '.html', '.xhtml') }).Count
    $inputs = @($files | Where-Object {
        $_.Extension -in @('.csv', '.dat', '.inp', '.input', '.json', '.xml', '.yaml', '.yml', '.sim', '.cas', '.msh', '.stl', '.step', '.iges', '.txt') -or
        $_.DirectoryName -match '(?i)(^|[\\/])(input|inputs|input_files|data)([\\/]|$)'
    }).Count
    $classified = @($files | Where-Object {
        $_.Extension -ieq '.java' -or
        $_.Extension -in @('.ps1', '.py', '.sh', '.bat', '.cmd', '.mjs', '.js') -or
        $_.Extension -in @('.md', '.txt', '.rst', '.html', '.xhtml')
    }).Count

    [PSCustomObject]@{
        GeneratedAt = (Get-Date).ToString('s')
        Category = $CategoryName
        CategoryLabel = $CategoryLabel
        ItemType = if ($isDirectory) { 'Directory' } else { 'File' }
        ItemName = $Item.Name
        RelativePath = $relative
        Description = Get-Description -DirectoryItem $Item
        HasREADME = if ($isDirectory) { Test-Path -LiteralPath (Join-Path $Item.FullName 'README.md') } else { $false }
        HasAGENTS = if ($isDirectory) { Test-Path -LiteralPath (Join-Path $Item.FullName 'AGENTS.md') } else { $false }
        JavaFileCount = $java
        ScriptFileCount = $scripts
        InputFileCount = $inputs
        DocumentationFileCount = $docs
        OtherFileCount = $files.Count - $classified
        TotalFileCount = $files.Count
    }
}

$ReferencesRoot = [IO.Path]::GetFullPath($ReferencesRoot)
$OutputPath = [IO.Path]::GetFullPath($OutputPath)
if (-not (Test-Path -LiteralPath $ReferencesRoot -PathType Container)) {
    throw "References root does not exist: $ReferencesRoot"
}

$rows = [System.Collections.Generic.List[object]]::new()
foreach ($entry in $categories.GetEnumerator()) {
    $categoryPath = Join-Path $ReferencesRoot $entry.Key
    if (-not (Test-Path -LiteralPath $categoryPath -PathType Container)) {
        Write-Warning "Category directory not found: $categoryPath"
        continue
    }

    $items = @(Get-ChildItem -LiteralPath $categoryPath -Force |
        Where-Object { $_.Name -notin $excludedDirectoryNames -and $_.Name -notlike '*.csv' })

    foreach ($item in $items) {
        $rows.Add((New-IndexRow -CategoryName $entry.Key -CategoryLabel $entry.Value -Item $item))
    }
}

$outputDirectory = Split-Path -Parent $OutputPath
New-Item -ItemType Directory -Path $outputDirectory -Force | Out-Null
$rows | Sort-Object Category, ItemType, ItemName |
    Export-Csv -LiteralPath $OutputPath -NoTypeInformation -Encoding UTF8

Write-Output "Generated $($rows.Count) index rows: $OutputPath"
